package com.appfit.ai

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.core.JsonValue
import com.anthropic.models.messages.ContentBlockParam
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.MessageParam
import com.anthropic.models.messages.TextBlockParam
import com.anthropic.models.messages.Tool
import com.anthropic.models.messages.ToolResultBlockParam
import com.anthropic.models.messages.ToolUnion
import com.anthropic.models.messages.ToolUseBlockParam
import com.appfit.ai.prompt.SystemPromptBuilder
import com.appfit.data.model.ChatMessage
import com.appfit.data.model.ChatRole
import com.appfit.data.model.DailyPlan
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.FavoriteRecipeRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class AiResponse(
    val text: String,
    val planModified: Boolean
)

@Singleton
class AnthropicService @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider,
    private val debugLogger: AiDebugLogger,
    private val toolApprovalManager: ToolApprovalManager,
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository,
    private val userProfileProvider: UserProfileProvider,
    private val favoriteRecipeRepository: FavoriteRecipeRepository
) : AiService {
    private val gson = Gson()
    private var cachedClient: AnthropicClient? = null
    private var cachedApiKey: String? = null

    private fun getClient(apiKey: String): AnthropicClient {
        if (cachedApiKey == apiKey && cachedClient != null) return cachedClient!!
        cachedClient = AnthropicOkHttpClient.builder().apiKey(apiKey).build()
        cachedApiKey = apiKey
        return cachedClient!!
    }

    override suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        currentPlan: DailyPlan,
        toolExecutor: ClaudeToolExecutor
    ): AiResponse = withContext(Dispatchers.IO) {

        val apiKey = apiKeyProvider.getApiKey()
            ?: throw IllegalStateException("API Key non configurata")

        debugLogger.clear()
        debugLogger.log("🚀 Invio messaggio: \"${userMessage.take(80)}\"")

        val client = getClient(apiKey)
        val userProfile = userProfileProvider.getProfile()
        val favorites = favoriteRecipeRepository.getAllFavoritesOnce()
        val systemPrompt = SystemPromptBuilder.build(currentPlan, userProfile, favorites)
        toolExecutor.resetModifiedFlag()

        val messages = mutableListOf<MessageParam>()
        conversationHistory.takeLast(10).forEach { msg ->
            messages.add(
                MessageParam.builder()
                    .role(if (msg.role == ChatRole.USER) MessageParam.Role.USER else MessageParam.Role.ASSISTANT)
                    .content(msg.content)
                    .build()
            )
        }
        messages.add(
            MessageParam.builder()
                .role(MessageParam.Role.USER)
                .content(userMessage)
                .build()
        )

        val tools = buildToolUnions()

        var currentMessages = messages.toMutableList()
        var iterations = 0
        val maxIterations = 8
        var finalText = ""

        while (iterations < maxIterations) {
            val params = MessageCreateParams.builder()
                .model(apiKeyProvider.getSelectedModel())
                .maxTokens(8192L)
                .system(systemPrompt)
                .messages(currentMessages)
                .tools(tools)
                .build()

            debugLogger.log("🔄 Iterazione $iterations → Claude (${currentMessages.size} msg in context)")
            val response = client.messages().create(params)
            iterations++

            val responseText = response.content()
                .filter { it.isText() }
                .joinToString("\n") { it.asText().text() }

            if (responseText.isNotBlank()) finalText = responseText

            val stopReason = response.stopReason().map { it.asString() }.orElse("")
            debugLogger.log("⏹ Stop reason: $stopReason")

            if (stopReason != "tool_use") break

            val toolUseBlocks = response.content().filter { it.isToolUse() }
            if (toolUseBlocks.isEmpty()) {
                debugLogger.log("⚠️ stop_reason=tool_use ma nessun tool_use block trovato")
                break
            }

            // Append assistant turn
            val assistantContentBlocks = response.content().mapNotNull { block ->
                when {
                    block.isText() -> ContentBlockParam.ofText(
                        TextBlockParam.builder().text(block.asText().text()).build()
                    )
                    block.isToolUse() -> {
                        val tb = block.asToolUse()
                        ContentBlockParam.ofToolUse(
                            ToolUseBlockParam.builder()
                                .id(tb.id())
                                .name(tb.name())
                                .input(tb._input())
                                .build()
                        )
                    }
                    else -> null
                }
            }
            currentMessages.add(
                MessageParam.builder()
                    .role(MessageParam.Role.ASSISTANT)
                    .content(MessageParam.Content.ofBlockParams(assistantContentBlocks))
                    .build()
            )

            // Separate silent tools (no approval) from write tools
            val silentTools = setOf("get_current_plan", "save_user_preferences", "update_user_notes", "save_workout_schedule", "update_profile_data", "add_reminder")
            val readOnlyBlocks = toolUseBlocks.filter { it.asToolUse().name() in silentTools }
            val writeBlocks = toolUseBlocks.filter { it.asToolUse().name() !in silentTools }

            val toolResultBlocks = mutableListOf<ContentBlockParam>()

            // Execute read-only tools immediately (no approval needed)
            for (block in readOnlyBlocks) {
                val tb = block.asToolUse()
                val inputJson = parseInputJson(tb)
                debugLogger.log("🔧 Tool (read): ${tb.name()}")
                val result = toolExecutor.execute(tb.name(), inputJson)
                debugLogger.log("   Risultato: ${result.take(150)}")
                toolResultBlocks.add(makeToolResult(tb.id(), result))
            }

            // For write tools: duplicate check + user approval
            if (writeBlocks.isNotEmpty()) {
                val approvalItems = writeBlocks.map { block ->
                    val tb = block.asToolUse()
                    val inputJson = parseInputJson(tb)
                    buildApprovalItem(tb.id(), tb.name(), inputJson)
                }
                debugLogger.log("⏸ In attesa approvazione utente (${approvalItems.size} azioni)")

                val approvalResult = toolApprovalManager.requestApproval(ApprovalRequest(approvalItems))
                debugLogger.log("▶ Risposta utente: ${if (approvalResult is ApprovalResult.Rejected) "RIFIUTATO" else "approvato"}")

                val itemsByToolId = when (approvalResult) {
                    is ApprovalResult.Approved -> approvalResult.items.associateBy { it.toolUseId }
                    is ApprovalResult.Rejected -> emptyMap()
                }

                for (block in writeBlocks) {
                    val tb = block.asToolUse()
                    val inputJson = parseInputJson(tb)
                    val item = itemsByToolId[tb.id()]

                    val resultStr = when {
                        item == null || !item.isSelected ->
                            "L'utente ha saltato questa modifica"

                        item.hasDuplicate && item.selectedDuplicateAction == DuplicateAction.REPLACE -> {
                            when (tb.name()) {
                                "add_activity" -> activityRepository.deleteActivity(item.duplicateId!!)
                                "update_meal" -> dietRepository.deleteMeal(item.duplicateId!!)
                            }
                            toolExecutor.execute(tb.name(), inputJson)
                        }

                        else -> toolExecutor.execute(tb.name(), inputJson)
                    }

                    debugLogger.log("🔧 Tool (write): ${tb.name()} → ${if (item?.isSelected == true) "✅" else "⏭"} ${resultStr.take(80)}")
                    toolResultBlocks.add(makeToolResult(tb.id(), resultStr))
                }
            }

            currentMessages.add(
                MessageParam.builder()
                    .role(MessageParam.Role.USER)
                    .content(MessageParam.Content.ofBlockParams(toolResultBlocks))
                    .build()
            )
        }

        val finalResponse = finalText.ifBlank { "Ho aggiornato il tuo piano." }
        debugLogger.log("✅ Risposta finale (${finalResponse.length} car.): ${finalResponse.take(80)}")
        debugLogger.log("📊 Tool eseguiti: ${if (toolExecutor.planModified) "piano modificato" else "nessuna modifica"}")

        AiResponse(
            text = finalResponse,
            planModified = toolExecutor.planModified
        )
    }

    private fun parseInputJson(tb: com.anthropic.models.messages.ToolUseBlock): String {
        return try {
            val inputMap = tb._input().convert(object : TypeReference<Map<String, Any>>() {})
            gson.toJson(inputMap)
        } catch (e: Exception) {
            debugLogger.log("❌ Parse input fallito per ${tb.name()}: ${e.message}")
            "{}"
        }
    }

    private fun makeToolResult(toolUseId: String, content: String): ContentBlockParam =
        ContentBlockParam.ofToolResult(
            ToolResultBlockParam.builder()
                .toolUseId(toolUseId)
                .content(content)
                .build()
        )

    private suspend fun buildApprovalItem(
        toolUseId: String,
        toolName: String,
        inputJson: String
    ): ApprovalItem {
        val input = gson.fromJson(inputJson, JsonObject::class.java)
        return when (toolName) {
            "add_activity" -> {
                val title = input["title"]?.asString ?: "Attività"
                val type = input["type"]?.asString ?: ""
                val dateStr = input["scheduled_date"]?.asString ?: LocalDate.now().toString()
                val dur = input["duration_minutes"]?.asInt ?: 0
                val date = try { LocalDate.parse(dateStr) } catch (e: Exception) { LocalDate.now() }
                val existingOnDate = activityRepository.getActivitiesForDate(date).first()
                // Priorità: 1) stesso tipo  2) stesso titolo (case-insensitive)
                val existing = existingOnDate.firstOrNull { it.type.name == type }
                    ?: existingOnDate.firstOrNull { it.title.equals(title, ignoreCase = true) }
                ApprovalItem(
                    toolUseId = toolUseId,
                    toolName = toolName,
                    inputJson = inputJson,
                    displayTitle = "Aggiungi: $title ($dur min)",
                    displayDetail = "$type · $dateStr",
                    hasDuplicate = existing != null,
                    duplicateId = existing?.id,
                    duplicateName = existing?.let { "${it.title} (${it.type.name})" }
                )
            }
            "update_activity" -> {
                val activityId = input["activity_id"]?.asLong ?: 0L
                val existing = activityRepository.getActivityById(activityId)
                val title = input["title"]?.asString ?: existing?.title ?: "Attività $activityId"
                val dateStr = input["scheduled_date"]?.asString ?: existing?.scheduledDate?.toString() ?: LocalDate.now().toString()
                val dur = input["duration_minutes"]?.asInt ?: existing?.durationMinutes ?: 0
                ApprovalItem(
                    toolUseId = toolUseId,
                    toolName = toolName,
                    inputJson = inputJson,
                    displayTitle = "Modifica: $title ($dur min)",
                    displayDetail = "ID $activityId · $dateStr"
                )
            }
            "update_meal" -> {
                val name = input["name"]?.asString ?: "Pasto"
                val type = input["type"]?.asString ?: ""
                val dateStr = input["scheduled_date"]?.asString ?: LocalDate.now().toString()
                val date = try { LocalDate.parse(dateStr) } catch (e: Exception) { LocalDate.now() }
                val existing = dietRepository.getMealsForDate(date).first()
                    .firstOrNull { it.type.name == type }
                ApprovalItem(
                    toolUseId = toolUseId,
                    toolName = toolName,
                    inputJson = inputJson,
                    displayTitle = "Aggiungi: $name",
                    displayDetail = "$type · $dateStr",
                    hasDuplicate = existing != null,
                    duplicateId = existing?.id,
                    duplicateName = existing?.name
                )
            }
            "delete_plan_item" -> {
                val itemType = input["item_type"]?.asString ?: ""
                val itemId = input["item_id"]?.asLong ?: 0L
                val label = when (itemType.uppercase()) {
                    "ACTIVITY" -> activityRepository.getActivityById(itemId)?.title ?: "Attività $itemId"
                    "MEAL" -> dietRepository.getMealById(itemId)?.name ?: "Pasto $itemId"
                    else -> "$itemType $itemId"
                }
                ApprovalItem(
                    toolUseId = toolUseId,
                    toolName = toolName,
                    inputJson = inputJson,
                    displayTitle = "Elimina: $label",
                    displayDetail = "$itemType · ID $itemId",
                    isDeleteAction = true
                )
            }
            else -> ApprovalItem(
                toolUseId = toolUseId,
                toolName = toolName,
                inputJson = inputJson,
                displayTitle = toolName,
                displayDetail = ""
            )
        }
    }

    private fun buildToolUnions(): List<ToolUnion> = buildTools().map { ToolUnion.ofTool(it) }

    private fun buildTools(): List<Tool> = listOf(
        buildTool(
            name = "add_activity",
            description = "Aggiunge una nuova attività fisica al calendario dell'utente",
            schemaJson = """{"type":"object","properties":{"title":{"type":"string"},"description":{"type":"string"},"type":{"type":"string","enum":["CARDIO","STRENGTH","FLEXIBILITY","YOGA","REST","CUSTOM"]},"duration_minutes":{"type":"integer"},"scheduled_date":{"type":"string"},"scheduled_time":{"type":"string"},"calories_burned":{"type":"integer"}},"required":["title","type","duration_minutes","scheduled_date"]}"""
        ),
        buildTool(
            name = "update_activity",
            description = "Modifica un'attività esistente nel calendario (titolo, tipo, durata, data, orario, calorie). Usa questo strumento quando l'utente vuole modificare o aggiornare un'attività già pianificata. Aggiorna solo i campi forniti, gli altri restano invariati. Sincronizza automaticamente con Google Calendar.",
            schemaJson = """{"type":"object","properties":{"activity_id":{"type":"integer","description":"ID dell'attività da aggiornare"},"title":{"type":"string"},"description":{"type":"string"},"type":{"type":"string","enum":["CARDIO","STRENGTH","FLEXIBILITY","YOGA","REST","CUSTOM"]},"duration_minutes":{"type":"integer"},"scheduled_date":{"type":"string","description":"Data in formato YYYY-MM-DD"},"scheduled_time":{"type":"string","description":"Orario in formato HH:mm"},"calories_burned":{"type":"integer"}},"required":["activity_id"]}"""
        ),
        buildTool(
            name = "update_meal",
            description = "Aggiunge o modifica un pasto nel piano dieta dell'utente",
            schemaJson = """{"type":"object","properties":{"name":{"type":"string"},"description":{"type":"string"},"type":{"type":"string","enum":["BREAKFAST","LUNCH","DINNER","SNACK"]},"scheduled_date":{"type":"string"},"ingredients":{"type":"array","items":{"type":"string"}},"calories_kcal":{"type":"integer"},"protein_g":{"type":"integer"},"carbs_g":{"type":"integer"},"fat_g":{"type":"integer"}},"required":["name","type","scheduled_date"]}"""
        ),
        buildTool(
            name = "delete_plan_item",
            description = "Rimuove un'attività o un pasto dal piano",
            schemaJson = """{"type":"object","properties":{"item_type":{"type":"string","enum":["ACTIVITY","MEAL"]},"item_id":{"type":"integer"}},"required":["item_type","item_id"]}"""
        ),
        buildTool(
            name = "get_current_plan",
            description = "Legge il piano attuale di una data specifica",
            schemaJson = """{"type":"object","properties":{"date":{"type":"string"}},"required":["date"]}"""
        ),
        buildTool(
            name = "save_user_preferences",
            description = "Salva le preferenze dell'utente rilevate dalla conversazione: tipi di allenamento preferiti, restrizioni alimentari, obiettivo fitness. Usare automaticamente senza chiedere conferma quando l'utente esprime preferenze, anche implicitamente.",
            schemaJson = """{"type":"object","properties":{"preferred_workout_types":{"type":"array","items":{"type":"string","enum":["CARDIO","STRENGTH","FLEXIBILITY","YOGA","REST","CUSTOM"]},"description":"Tipi di allenamento preferiti dall'utente"},"dietary_restrictions":{"type":"array","items":{"type":"string"},"description":"Restrizioni o preferenze alimentari, es: vegetariano, vegano, senza glutine, senza lattosio, halal, kosher"},"fitness_goal":{"type":"string","enum":["weight_loss","muscle_gain","endurance","flexibility","general_health"],"description":"Obiettivo fitness principale dell'utente"}},"required":[]}"""
        ),
        buildTool(
            name = "update_user_notes",
            description = "Gestisce le note personali dell'utente. Usa questo strumento automaticamente quando l'utente condivide informazioni personali rilevanti (es. infortuni, preferenze specifiche, condizioni mediche, orari preferiti, ecc.) che non rientrano nelle preferenze standard. Puoi aggiungere, modificare, eliminare singole note o svuotare tutte le note.",
            schemaJson = """{"type":"object","properties":{"action":{"type":"string","enum":["add","update","delete","clear"],"description":"Operazione da eseguire"},"id":{"type":"string","description":"ID della nota (obbligatorio per update e delete)"},"content":{"type":"string","description":"Contenuto della nota (obbligatorio per add e update)"}},"required":["action"]}"""
        ),
        buildTool(
            name = "save_workout_schedule",
            description = "Salva le fasce orarie e i giorni di allenamento dell'utente. Usa questo strumento automaticamente quando l'utente indica i giorni e/o gli orari in cui preferisce allenarsi. Sostituisce completamente la schedule precedente. Per rimuovere tutto, passa slots vuoto.",
            schemaJson = """{"type":"object","properties":{"slots":{"type":"array","description":"Lista di fasce orarie di allenamento","items":{"type":"object","properties":{"days":{"type":"array","items":{"type":"string","enum":["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"]},"description":"Giorni della settimana"},"start_time":{"type":"string","description":"Orario di inizio in formato HH:mm, es: 07:00"},"end_time":{"type":"string","description":"Orario di fine in formato HH:mm, es: 09:00"}},"required":["days"]}}},"required":["slots"]}"""
        ),
        buildTool(
            name = "add_reminder",
            description = "Aggiunge un promemoria (bolletta, scadenza auto, documento, abbonamento, ecc.). Usa automaticamente quando l'utente menziona scadenze da ricordare.",
            schemaJson = """{"type":"object","properties":{"title":{"type":"string"},"description":{"type":"string"},"category":{"type":"string","enum":["BILL","CAR","DOCUMENT","HEALTH","SUBSCRIPTION","OTHER"]},"due_date":{"type":"string","description":"Data scadenza YYYY-MM-DD, null se non nota"},"amount":{"type":"number","description":"Importo in euro, null se non noto"},"is_important":{"type":"boolean","description":"true se è urgente/critico"}},"required":["title","category"]}"""
        ),
        buildTool(
            name = "update_profile_data",
            description = "Salva i dati fisici e il livello di forma dell'utente. Usa questo strumento dopo aver raccolto i dati mancanti (sesso, peso, altezza, età, livello di forma fisica) necessari per generare un piano. Aggiorna solo i campi forniti, gli altri restano invariati.",
            schemaJson = """{"type":"object","properties":{"sex":{"type":"string","enum":["male","female"],"description":"Sesso biologico dell'utente"},"weight_kg":{"type":"number","description":"Peso in kg, es: 75.5"},"height_cm":{"type":"integer","description":"Altezza in cm, es: 175"},"age":{"type":"integer","description":"Età in anni"},"fitness_level":{"type":"string","enum":["beginner","intermediate","advanced"],"description":"Livello di forma fisica: beginner=principiante, intermediate=intermedio, advanced=avanzato"}},"required":[]}"""
        )
    )

    @Suppress("UNCHECKED_CAST")
    private fun buildTool(name: String, description: String, schemaJson: String): Tool {
        val schemaMap = gson.fromJson(schemaJson, Map::class.java) as Map<String, Any>
        return Tool.builder()
            .name(name)
            .description(description)
            .inputSchema(
                Tool.InputSchema.builder()
                    .putAllAdditionalProperties(
                        schemaMap.mapValues { JsonValue.from(it.value) }
                    )
                    .build()
            )
            .build()
    }
}

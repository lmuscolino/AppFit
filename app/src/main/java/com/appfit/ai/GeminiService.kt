package com.appfit.ai

import com.appfit.ai.prompt.SystemPromptBuilder
import com.appfit.data.model.ChatMessage
import com.appfit.data.model.ChatRole
import com.appfit.data.model.DailyPlan
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.FavoriteRecipeRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider,
    private val debugLogger: AiDebugLogger,
    private val toolApprovalManager: ToolApprovalManager,
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository,
    private val userProfileProvider: UserProfileProvider,
    private val favoriteRecipeRepository: FavoriteRecipeRepository
) : AiService {

    private val gson = Gson()

    override suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        currentPlan: DailyPlan,
        toolExecutor: ClaudeToolExecutor
    ): AiResponse = withContext(Dispatchers.IO) {

        val apiKey = apiKeyProvider.getGeminiApiKey()
            ?: throw IllegalStateException("Gemini API Key non configurata")
        val model = apiKeyProvider.getSelectedModel()

        debugLogger.clear()
        debugLogger.log("🚀 Gemini — Invio messaggio: \"${userMessage.take(80)}\"")

        val userProfile = userProfileProvider.getProfile()
        val favorites = favoriteRecipeRepository.getAllFavoritesOnce()
        val systemPrompt = SystemPromptBuilder.build(currentPlan, userProfile, favorites)
        toolExecutor.resetModifiedFlag()

        // Build conversation contents — Gemini requires strictly alternating user/model turns.
        // Filter history to maintain alternation, then append the new user message.
        val contents = JsonArray()
        val sanitized = mutableListOf<ChatMessage>()
        for (msg in conversationHistory.takeLast(10)) {
            if (sanitized.isEmpty() || sanitized.last().role != msg.role) {
                sanitized.add(msg)
            }
            // Skip consecutive same-role messages (keep only the last one implicitly via below)
        }
        // Gemini contents must start with a user turn
        val startIdx = sanitized.indexOfFirst { it.role == ChatRole.USER }
        if (startIdx >= 0) {
            for (msg in sanitized.drop(startIdx)) {
                contents.add(JsonObject().apply {
                    addProperty("role", if (msg.role == ChatRole.USER) "user" else "model")
                    add("parts", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("text", msg.content.ifBlank { "." })
                        })
                    })
                })
            }
        }
        // The new user message must follow a model turn (or be first)
        val lastRole = if (contents.size() > 0)
            contents[contents.size() - 1].asJsonObject.get("role").asString
        else null
        if (lastRole == "user") {
            // Merge with previous user turn rather than creating two consecutive user turns
            contents[contents.size() - 1].asJsonObject
                .getAsJsonArray("parts")
                .add(JsonObject().apply { addProperty("text", "\n$userMessage") })
        } else {
            contents.add(JsonObject().apply {
                addProperty("role", "user")
                add("parts", JsonArray().apply {
                    add(JsonObject().apply { addProperty("text", userMessage) })
                })
            })
        }

        val tools = buildGeminiTools()
        var iterations = 0
        val maxIterations = 8
        var finalText = ""

        while (iterations < maxIterations) {
            debugLogger.log("🔄 Iterazione $iterations → Gemini (${contents.size()} turn)")

            val requestBody = JsonObject().apply {
                add("system_instruction", JsonObject().apply {
                    add("parts", JsonArray().apply {
                        add(JsonObject().apply { addProperty("text", systemPrompt) })
                    })
                })
                add("contents", contents)
                add("tools", tools)
            }

            val response = try {
                callGeminiApi(apiKey, model, requestBody)
            } catch (e: Exception) {
                debugLogger.log("❌ Errore chiamata Gemini: ${e.message}")
                throw e
            }
            iterations++

            // Check for prompt-level blocks (safety filters etc.)
            val promptFeedback = response.getAsJsonObject("promptFeedback")
            val blockReason = promptFeedback?.get("blockReason")?.asString
            if (blockReason != null) {
                debugLogger.log("🚫 Gemini ha bloccato la richiesta: $blockReason")
                finalText = "Il messaggio è stato bloccato dai filtri di sicurezza Gemini ($blockReason)."
                break
            }

            val candidates = response.getAsJsonArray("candidates")
            if (candidates == null || candidates.size() == 0) {
                debugLogger.log("❌ Nessun candidato nella risposta Gemini")
                finalText = finalText.ifBlank { "Gemini non ha restituito una risposta. Riprova." }
                break
            }

            val candidate = candidates[0].asJsonObject
            val content = candidate.getAsJsonObject("content") ?: break
            val parts = content.getAsJsonArray("parts") ?: break
            val finishReason = candidate.get("finishReason")?.asString ?: "STOP"

            debugLogger.log("⏹ Finish reason: $finishReason")

            // Collect text and function calls
            val textParts = mutableListOf<String>()
            val functionCallParts = mutableListOf<JsonObject>()

            for (part in parts) {
                val p = part.asJsonObject
                when {
                    p.has("text") -> textParts.add(p.get("text").asString)
                    p.has("functionCall") -> functionCallParts.add(p.getAsJsonObject("functionCall"))
                }
            }

            val responseText = textParts.joinToString("\n")
            if (responseText.isNotBlank()) finalText = responseText

            if (functionCallParts.isEmpty()) break

            // Append model turn (with function calls)
            contents.add(JsonObject().apply {
                addProperty("role", "model")
                add("parts", parts)
            })

            // Process tool calls
            val silentTools = setOf("get_current_plan", "save_user_preferences", "update_user_notes", "save_workout_schedule", "update_profile_data", "add_reminder")
            val readOnlyFc = functionCallParts.filter { it.get("name").asString in silentTools }
            val writeFc = functionCallParts.filter { it.get("name").asString !in silentTools }

            val functionResponses = JsonArray()

            // Read-only tools — execute immediately
            for (fc in readOnlyFc) {
                val name = fc.get("name").asString
                val args = fc.getAsJsonObject("args") ?: JsonObject()
                val inputJson = gson.toJson(args)
                debugLogger.log("🔧 Tool (read): $name")
                val result = toolExecutor.execute(name, inputJson)
                debugLogger.log("   Risultato: ${result.take(150)}")
                functionResponses.add(buildFunctionResponse(name, result))
            }

            // Write tools — request user approval
            if (writeFc.isNotEmpty()) {
                val approvalItems = writeFc.map { fc ->
                    val name = fc.get("name").asString
                    val args = fc.getAsJsonObject("args") ?: JsonObject()
                    val inputJson = gson.toJson(args)
                    // Use a synthetic toolUseId (Gemini doesn't provide one)
                    val syntheticId = "${name}_${System.currentTimeMillis()}"
                    buildApprovalItem(syntheticId, name, inputJson)
                }
                debugLogger.log("⏸ In attesa approvazione utente (${approvalItems.size} azioni)")

                val approvalResult = toolApprovalManager.requestApproval(ApprovalRequest(approvalItems))
                debugLogger.log("▶ Risposta utente: ${if (approvalResult is ApprovalResult.Rejected) "RIFIUTATO" else "approvato"}")

                val itemsByToolId = when (approvalResult) {
                    is ApprovalResult.Approved -> approvalResult.items.associateBy { it.toolUseId }
                    is ApprovalResult.Rejected -> emptyMap()
                }

                for ((idx, fc) in writeFc.withIndex()) {
                    val name = fc.get("name").asString
                    val args = fc.getAsJsonObject("args") ?: JsonObject()
                    val inputJson = gson.toJson(args)
                    val syntheticId = "${name}_${System.currentTimeMillis() + idx}"
                    // Find approval item — match by toolName since Gemini has no IDs
                    val item = approvalResult.let {
                        if (it is ApprovalResult.Approved)
                            it.items.firstOrNull { i -> i.toolName == name && i.isSelected }
                        else null
                    }

                    val resultStr = when {
                        item == null ->
                            "L'utente ha saltato questa modifica"

                        item.hasDuplicate && item.selectedDuplicateAction == DuplicateAction.REPLACE -> {
                            when (name) {
                                "add_activity" -> activityRepository.deleteActivity(item.duplicateId!!)
                                "update_meal" -> dietRepository.deleteMeal(item.duplicateId!!)
                            }
                            toolExecutor.execute(name, inputJson)
                        }

                        else -> toolExecutor.execute(name, inputJson)
                    }

                    debugLogger.log("🔧 Tool (write): $name → ${if (item != null) "✅" else "⏭"} ${resultStr.take(80)}")
                    functionResponses.add(buildFunctionResponse(name, resultStr))
                }
            }

            // Append user turn with function responses
            contents.add(JsonObject().apply {
                addProperty("role", "user")
                add("parts", functionResponses)
            })
        }

        val finalResponse = finalText.ifBlank { "Ho aggiornato il tuo piano." }
        debugLogger.log("✅ Risposta finale (${finalResponse.length} car.): ${finalResponse.take(80)}")

        AiResponse(
            text = finalResponse,
            planModified = toolExecutor.planModified
        )
    }

    private fun callGeminiApi(apiKey: String, model: String, body: JsonObject): JsonObject {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val conn = URL(endpoint).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.doInput = true
            conn.connectTimeout = 30_000
            conn.readTimeout = 60_000

            // Write request body
            try {
                OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use {
                    it.write(gson.toJson(body))
                }
            } catch (e: Exception) {
                throw RuntimeException("Gemini: errore scrittura richiesta (${e.javaClass.simpleName}): ${e.message}")
            }

            // Read status code — on Android this can throw FileNotFoundException for 4xx/5xx
            val statusCode = try {
                conn.responseCode
            } catch (e: java.io.FileNotFoundException) {
                val errBody = conn.errorStream?.bufferedReader(Charsets.UTF_8)?.readText()
                    ?: "nessun dettaglio"
                throw RuntimeException("Gemini API errore (FNF): $errBody")
            } catch (e: Exception) {
                throw RuntimeException("Gemini: errore lettura status (${e.javaClass.simpleName}): ${e.message}")
            }

            // Read response body from correct stream
            val responseBody = try {
                if (statusCode in 200..299) {
                    conn.inputStream.bufferedReader(Charsets.UTF_8).readText()
                } else {
                    conn.errorStream?.bufferedReader(Charsets.UTF_8)?.readText()
                        ?: "HTTP $statusCode senza corpo"
                }
            } catch (e: java.io.FileNotFoundException) {
                conn.errorStream?.bufferedReader(Charsets.UTF_8)?.readText()
                    ?: "HTTP $statusCode (FNF, nessun errorStream)"
            } catch (e: Exception) {
                "HTTP $statusCode — errore lettura corpo: ${e.message}"
            }

            if (statusCode !in 200..299) {
                throw RuntimeException("Gemini HTTP $statusCode: $responseBody")
            }

            return try {
                JsonParser.parseString(responseBody).asJsonObject
            } catch (e: Exception) {
                throw RuntimeException("Gemini: JSON non valido nella risposta: ${responseBody.take(200)}")
            }

        } finally {
            conn.disconnect()
        }
    }

    private fun buildFunctionResponse(name: String, result: String): JsonObject =
        JsonObject().apply {
            add("functionResponse", JsonObject().apply {
                addProperty("name", name)
                add("response", JsonObject().apply {
                    addProperty("result", result)
                })
            })
        }

    private fun buildGeminiTools(): JsonArray {
        val declarations = JsonArray()
        listOf(
            Triple(
                "add_activity",
                "Aggiunge una nuova attività fisica al calendario dell'utente",
                """{"type":"object","properties":{"title":{"type":"string"},"description":{"type":"string"},"type":{"type":"string","enum":["CARDIO","STRENGTH","FLEXIBILITY","YOGA","REST","CUSTOM"]},"duration_minutes":{"type":"integer"},"scheduled_date":{"type":"string"},"scheduled_time":{"type":"string"},"calories_burned":{"type":"integer"}},"required":["title","type","duration_minutes","scheduled_date"]}"""
            ),
            Triple(
                "update_activity",
                "Modifica un'attività esistente nel calendario (titolo, tipo, durata, data, orario, calorie). Usa quando l'utente vuole modificare un'attività già pianificata. Aggiorna solo i campi forniti. Sincronizza automaticamente con Google Calendar.",
                """{"type":"object","properties":{"activity_id":{"type":"integer","description":"ID dell'attività da aggiornare"},"title":{"type":"string"},"description":{"type":"string"},"type":{"type":"string","enum":["CARDIO","STRENGTH","FLEXIBILITY","YOGA","REST","CUSTOM"]},"duration_minutes":{"type":"integer"},"scheduled_date":{"type":"string","description":"Data in formato YYYY-MM-DD"},"scheduled_time":{"type":"string","description":"Orario in formato HH:mm"},"calories_burned":{"type":"integer"}},"required":["activity_id"]}"""
            ),
            Triple(
                "update_meal",
                "Aggiunge o modifica un pasto nel piano dieta dell'utente",
                """{"type":"object","properties":{"name":{"type":"string"},"description":{"type":"string"},"type":{"type":"string","enum":["BREAKFAST","LUNCH","DINNER","SNACK"]},"scheduled_date":{"type":"string"},"ingredients":{"type":"array","items":{"type":"string"}},"calories_kcal":{"type":"integer"},"protein_g":{"type":"integer"},"carbs_g":{"type":"integer"},"fat_g":{"type":"integer"}},"required":["name","type","scheduled_date"]}"""
            ),
            Triple(
                "delete_plan_item",
                "Rimuove un'attività o un pasto dal piano",
                """{"type":"object","properties":{"item_type":{"type":"string","enum":["ACTIVITY","MEAL"]},"item_id":{"type":"integer"}},"required":["item_type","item_id"]}"""
            ),
            Triple(
                "get_current_plan",
                "Legge il piano attuale di una data specifica",
                """{"type":"object","properties":{"date":{"type":"string"}},"required":["date"]}"""
            ),
            Triple(
                "save_user_preferences",
                "Salva le preferenze dell'utente rilevate dalla conversazione",
                """{"type":"object","properties":{"preferred_workout_types":{"type":"array","items":{"type":"string","enum":["CARDIO","STRENGTH","FLEXIBILITY","YOGA","REST","CUSTOM"]}},"dietary_restrictions":{"type":"array","items":{"type":"string"}},"fitness_goal":{"type":"string","enum":["weight_loss","muscle_gain","endurance","flexibility","general_health"]}},"required":[]}"""
            ),
            Triple(
                "update_user_notes",
                "Gestisce le note personali dell'utente. Usa automaticamente quando l'utente condivide info personali rilevanti (infortuni, condizioni mediche, orari preferiti, ecc.)",
                """{"type":"object","properties":{"action":{"type":"string","enum":["add","update","delete","clear"]},"id":{"type":"string"},"content":{"type":"string"}},"required":["action"]}"""
            ),
            Triple(
                "save_workout_schedule",
                "Salva le fasce orarie e i giorni di allenamento dell'utente. Usa automaticamente quando l'utente indica giorni e/o orari preferiti per allenarsi. Sostituisce completamente la schedule precedente.",
                """{"type":"object","properties":{"slots":{"type":"array","items":{"type":"object","properties":{"days":{"type":"array","items":{"type":"string","enum":["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"]}},"start_time":{"type":"string"},"end_time":{"type":"string"}},"required":["days"]}}},"required":["slots"]}"""
            ),
            Triple(
                "add_reminder",
                "Aggiunge un promemoria (bolletta, scadenza auto, documento, abbonamento, ecc.). Usa automaticamente quando l'utente menziona scadenze da ricordare.",
                """{"type":"object","properties":{"title":{"type":"string"},"description":{"type":"string"},"category":{"type":"string","enum":["BILL","CAR","DOCUMENT","HEALTH","SUBSCRIPTION","OTHER"]},"due_date":{"type":"string"},"amount":{"type":"number"},"is_important":{"type":"boolean"}},"required":["title","category"]}"""
            ),
            Triple(
                "update_profile_data",
                "Salva i dati fisici e il livello di forma dell'utente. Usa dopo aver raccolto i dati mancanti necessari per generare un piano. Aggiorna solo i campi forniti.",
                """{"type":"object","properties":{"sex":{"type":"string","enum":["male","female"]},"weight_kg":{"type":"number"},"height_cm":{"type":"integer"},"age":{"type":"integer"},"fitness_level":{"type":"string","enum":["beginner","intermediate","advanced"]}},"required":[]}"""
            )
        ).forEach { (name, description, schemaJson) ->
            declarations.add(JsonObject().apply {
                addProperty("name", name)
                addProperty("description", description)
                add("parameters", JsonParser.parseString(schemaJson).asJsonObject)
            })
        }
        return JsonArray().apply {
            add(JsonObject().apply { add("functionDeclarations", declarations) })
        }
    }

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
}

package com.appfit.ai

import com.appfit.ai.WorkoutScheduleSlot
import com.appfit.data.model.*
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.ReminderRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClaudeToolExecutor @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository,
    private val userProfileProvider: UserProfileProvider,
    private val googleCalendarService: GoogleCalendarService,
    private val reminderRepository: ReminderRepository
) {
    private val gson = Gson()

    var planModified = false
        private set

    fun resetModifiedFlag() {
        planModified = false
    }

    suspend fun execute(toolName: String, inputJson: String): String {
        return try {
            val input = gson.fromJson(inputJson, JsonObject::class.java)
            when (toolName) {
                "add_activity"           -> handleAddActivity(input)
                "update_activity"        -> handleUpdateActivity(input)
                "update_meal"            -> handleUpdateMeal(input)
                "delete_plan_item"       -> handleDeleteItem(input)
                "get_current_plan"       -> handleGetPlan(input)
                "save_user_preferences"  -> handleSavePreferences(input)
                "update_user_notes"      -> handleUpdateUserNotes(input)
                "save_workout_schedule"  -> handleSaveWorkoutSchedule(input)
                "update_profile_data"    -> handleUpdateProfileData(input)
                "add_reminder"           -> handleAddReminder(input)
                else -> "Strumento sconosciuto: $toolName"
            }
        } catch (e: Exception) {
            "Errore nell'esecuzione dello strumento: ${e.message}"
        }
    }

    private suspend fun handleUpdateActivity(input: JsonObject): String {
        val activityId = input.get("activity_id")?.asLong ?: return "Errore: activity_id mancante"
        val existing = activityRepository.getActivityById(activityId) ?: return "Errore: attività ID:$activityId non trovata"

        val title = input.get("title")?.asString ?: existing.title
        val description = input.get("description")?.asString ?: existing.description
        val typeStr = input.get("type")?.asString
        val type = if (typeStr != null) try { ActivityType.valueOf(typeStr) } catch (e: Exception) { existing.type } else existing.type
        val durationMinutes = input.get("duration_minutes")?.asInt ?: existing.durationMinutes
        val scheduledDateStr = input.get("scheduled_date")?.asString
        val scheduledDate = if (scheduledDateStr != null) try { LocalDate.parse(scheduledDateStr) } catch (e: Exception) { existing.scheduledDate } else existing.scheduledDate
        val scheduledTimeStr = input.get("scheduled_time")?.asString
        val scheduledTime = when {
            scheduledTimeStr != null -> try { LocalTime.parse(scheduledTimeStr) } catch (e: Exception) { existing.scheduledTime }
            else -> existing.scheduledTime
        }
        val caloriesBurned = input.get("calories_burned")?.asInt ?: existing.caloriesBurned

        val updated = existing.copy(
            title = title,
            description = description,
            type = type,
            durationMinutes = durationMinutes,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            caloriesBurned = caloriesBurned
        )

        activityRepository.updateActivity(updated)
        planModified = true
        googleCalendarService.syncActivity(updated)
        return "Attività ID:$activityId aggiornata: '$title' il $scheduledDate"
    }

    private suspend fun handleAddActivity(input: JsonObject): String {
        val title = input.get("title")?.asString ?: return "Errore: title mancante"
        val description = input.get("description")?.asString ?: ""
        val typeStr = input.get("type")?.asString ?: "CUSTOM"
        val durationMinutes = input.get("duration_minutes")?.asInt ?: 30
        val scheduledDateStr = input.get("scheduled_date")?.asString ?: LocalDate.now().toString()
        val scheduledTimeStr = input.get("scheduled_time")?.asString
        val caloriesBurned = input.get("calories_burned")?.asInt ?: 0

        val type = try { ActivityType.valueOf(typeStr) } catch (e: Exception) { ActivityType.CUSTOM }
        val scheduledDate = try { LocalDate.parse(scheduledDateStr) } catch (e: Exception) { LocalDate.now() }
        val scheduledTime = scheduledTimeStr?.let {
            try { LocalTime.parse(it) } catch (e: Exception) { null }
        }

        val activity = Activity(
            title = title,
            description = description,
            type = type,
            durationMinutes = durationMinutes,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            caloriesBurned = caloriesBurned,
            aiGenerated = true
        )

        val id = activityRepository.insertActivity(activity)
        planModified = true
        // Sync to Google Calendar if enabled (fire-and-forget, non-blocking)
        googleCalendarService.syncActivity(activity.copy(id = id))
        return "Attività aggiunta con successo: '$title' il $scheduledDateStr (ID: $id)"
    }

    private suspend fun handleUpdateMeal(input: JsonObject): String {
        val name = input.get("name")?.asString ?: return "Errore: name mancante"
        val description = input.get("description")?.asString ?: ""
        val typeStr = input.get("type")?.asString ?: "LUNCH"
        val scheduledDateStr = input.get("scheduled_date")?.asString ?: LocalDate.now().toString()
        val caloriesKcal = input.get("calories_kcal")?.asInt ?: 0
        val proteinG = input.get("protein_g")?.asInt ?: 0
        val carbsG = input.get("carbs_g")?.asInt ?: 0
        val fatG = input.get("fat_g")?.asInt ?: 0

        val ingredientsArray = input.getAsJsonArray("ingredients")
        val ingredients = ingredientsArray?.map { it.asString } ?: emptyList()

        val type = try { MealType.valueOf(typeStr) } catch (e: Exception) { MealType.LUNCH }
        val scheduledDate = try { LocalDate.parse(scheduledDateStr) } catch (e: Exception) { LocalDate.now() }

        val meal = Meal(
            name = name,
            description = description,
            type = type,
            scheduledDate = scheduledDate,
            ingredients = ingredients,
            caloriesKcal = caloriesKcal,
            proteinG = proteinG,
            carbsG = carbsG,
            fatG = fatG,
            aiGenerated = true
        )

        val id = dietRepository.insertMeal(meal)
        planModified = true
        return "Pasto aggiunto con successo: '$name' (${type.displayName()}) il $scheduledDateStr (ID: $id)"
    }

    private suspend fun handleDeleteItem(input: JsonObject): String {
        val itemType = input.get("item_type")?.asString ?: return "Errore: item_type mancante"
        val itemId = input.get("item_id")?.asLong ?: return "Errore: item_id mancante"

        return when (itemType.uppercase()) {
            "ACTIVITY" -> {
                googleCalendarService.deleteActivityEvent(itemId)
                activityRepository.deleteActivity(itemId)
                planModified = true
                "Attività ID:$itemId eliminata con successo"
            }
            "MEAL" -> {
                dietRepository.deleteMeal(itemId)
                planModified = true
                "Pasto ID:$itemId eliminato con successo"
            }
            else -> "Tipo sconosciuto: $itemType. Usa ACTIVITY o MEAL."
        }
    }

    private suspend fun handleGetPlan(input: JsonObject): String {
        val dateStr = input.get("date")?.asString ?: LocalDate.now().toString()
        val date = try { LocalDate.parse(dateStr) } catch (e: Exception) { LocalDate.now() }

        val activities = activityRepository.getActivitiesForDate(date).first()
        val meals = dietRepository.getMealsForDate(date).first()

        val sb = StringBuilder("Piano del $dateStr:\n")

        if (activities.isEmpty() && meals.isEmpty()) {
            sb.appendLine("Nessuna attività o pasto pianificato per questa data.")
        } else {
            if (activities.isNotEmpty()) {
                sb.appendLine("Attività:")
                activities.forEach { a ->
                    sb.appendLine("  - ID:${a.id} | ${a.title} | ${a.type.name} | ${a.durationMinutes}min | Completata: ${a.isCompleted}")
                }
            }
            if (meals.isNotEmpty()) {
                sb.appendLine("Pasti:")
                meals.forEach { m ->
                    sb.appendLine("  - ID:${m.id} | ${m.type.displayName()}: ${m.name} | ${m.caloriesKcal}kcal")
                }
            }
        }

        return sb.toString()
    }

    private suspend fun handleUpdateUserNotes(input: JsonObject): String {
        val action = input.get("action")?.asString ?: return "Errore: action mancante"
        return when (action) {
            "add" -> {
                val content = input.get("content")?.asString ?: return "Errore: content mancante"
                userProfileProvider.addNote(content)
                "Nota aggiunta: \"$content\""
            }
            "update" -> {
                val id = input.get("id")?.asString ?: return "Errore: id mancante"
                val content = input.get("content")?.asString ?: return "Errore: content mancante"
                userProfileProvider.updateNote(id, content)
                "Nota aggiornata"
            }
            "delete" -> {
                val id = input.get("id")?.asString ?: return "Errore: id mancante"
                userProfileProvider.deleteNote(id)
                "Nota eliminata"
            }
            "clear" -> {
                userProfileProvider.setNotes(emptyList())
                "Tutte le note eliminate"
            }
            else -> "Azione sconosciuta: $action"
        }
    }

    private suspend fun handleSaveWorkoutSchedule(input: JsonObject): String {
        val slotsArray = input.getAsJsonArray("slots") ?: return "Errore: slots mancanti"
        val slots = slotsArray.mapNotNull { el ->
            val obj = el.asJsonObject ?: return@mapNotNull null
            val daysArray = obj.getAsJsonArray("days") ?: return@mapNotNull null
            val days = daysArray.map { it.asString }.toSet()
            val startTime = obj.get("start_time")?.asString ?: ""
            val endTime = obj.get("end_time")?.asString ?: ""
            if (days.isEmpty()) null else WorkoutScheduleSlot(days, startTime, endTime)
        }
        userProfileProvider.saveWorkoutSchedule(slots)
        return if (slots.isEmpty()) {
            "Fasce orarie di allenamento rimosse"
        } else {
            val summary = slots.joinToString("; ") { slot ->
                val dayLabels = slot.days.joinToString(", ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
                "$dayLabels ${slot.startTime}–${slot.endTime}"
            }
            "Fasce orarie di allenamento salvate: $summary"
        }
    }

    private suspend fun handleAddReminder(input: JsonObject): String {
        val title = input.get("title")?.asString ?: return "Errore: title mancante"
        val description = input.get("description")?.asString ?: ""
        val categoryStr = input.get("category")?.asString ?: "OTHER"
        val category = try { ReminderCategory.valueOf(categoryStr) } catch (e: Exception) { ReminderCategory.OTHER }
        val dueDateStr = input.get("due_date")?.takeIf { !it.isJsonNull }?.asString
        val dueDate = dueDateStr?.let { runCatching { java.time.LocalDate.parse(it) }.getOrNull() }
        val amount = input.get("amount")?.takeIf { !it.isJsonNull }?.asFloat
        val isImportant = input.get("is_important")?.asBoolean ?: false

        val reminder = Reminder(
            title = title,
            description = description,
            category = category,
            dueDate = dueDate,
            amount = amount,
            isImportant = isImportant
        )
        val id = reminderRepository.insertReminder(reminder)
        return "Promemoria aggiunto: '$title' (${category.displayName()})${if (dueDate != null) " — scadenza $dueDate" else ""} (ID: $id)"
    }

    private suspend fun handleUpdateProfileData(input: JsonObject): String {
        val sex = input.get("sex")?.asString
        val weightKg = input.get("weight_kg")?.asFloat
        val heightCm = input.get("height_cm")?.asInt
        val age = input.get("age")?.asInt
        val fitnessLevel = input.get("fitness_level")?.asString

        userProfileProvider.savePhysicalProfile(sex, weightKg, heightCm, age, fitnessLevel)

        val parts = mutableListOf<String>()
        if (sex != null) parts.add("sesso: ${if (sex == "male") "maschio" else "femmina"}")
        if (weightKg != null) parts.add("peso: ${weightKg.toInt()} kg")
        if (heightCm != null) parts.add("altezza: $heightCm cm")
        if (age != null) parts.add("età: $age anni")
        if (fitnessLevel != null) parts.add("forma fisica: $fitnessLevel")

        return "Profilo aggiornato — ${parts.joinToString(", ")}"
    }

    private suspend fun handleSavePreferences(input: JsonObject): String {
        val workoutTypesArray = input.getAsJsonArray("preferred_workout_types")
        val workoutTypes = workoutTypesArray?.map { it.asString }

        val dietaryArray = input.getAsJsonArray("dietary_restrictions")
        val dietaryRestrictions = dietaryArray?.map { it.asString }

        val fitnessGoal = input.get("fitness_goal")?.asString

        userProfileProvider.savePreferences(workoutTypes, dietaryRestrictions, fitnessGoal)

        val parts = mutableListOf<String>()
        if (!workoutTypes.isNullOrEmpty()) parts.add("allenamenti preferiti: ${workoutTypes.joinToString(", ")}")
        if (!dietaryRestrictions.isNullOrEmpty()) parts.add("restrizioni alimentari: ${dietaryRestrictions.joinToString(", ")}")
        if (fitnessGoal != null) parts.add("obiettivo: $fitnessGoal")

        return "Preferenze utente salvate — ${parts.joinToString("; ")}"
    }
}

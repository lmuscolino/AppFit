package com.appfit.ai

import com.appfit.data.model.*
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
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
    private val userProfileProvider: UserProfileProvider
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
                "update_meal"            -> handleUpdateMeal(input)
                "delete_plan_item"       -> handleDeleteItem(input)
                "get_current_plan"       -> handleGetPlan(input)
                "save_user_preferences"  -> handleSavePreferences(input)
                else -> "Strumento sconosciuto: $toolName"
            }
        } catch (e: Exception) {
            "Errore nell'esecuzione dello strumento: ${e.message}"
        }
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

package com.appfit.ai

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class UserNote(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val createdAt: Instant = Instant.now()
)

/**
 * Rappresenta una fascia oraria ricorrente di allenamento.
 * [days]: insieme di giorni ("MONDAY", "TUESDAY", …)
 * [startTime]: orario di inizio in formato "HH:mm"
 * [endTime]: orario di fine in formato "HH:mm"
 */
data class WorkoutScheduleSlot(
    val days: Set<String> = emptySet(),
    val startTime: String = "",
    val endTime: String = ""
)

data class UserProfile(
    val sex: String? = null,           // "male" | "female"
    val weightKg: Float?,
    val heightCm: Int?,
    val age: Int?,
    val monthlyUpdateEnabled: Boolean,
    val preferredWorkoutTypes: List<String> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val fitnessGoal: String? = null,
    val fitnessLevel: String? = null,  // "beginner" | "intermediate" | "advanced"
    val userNotes: List<UserNote> = emptyList(),
    val workoutSchedule: List<WorkoutScheduleSlot> = emptyList()
)

@Singleton
class UserProfileProvider @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val gson = Gson()

    companion object {
        private val SEX = stringPreferencesKey("user_sex")
        private val WEIGHT_KG = floatPreferencesKey("user_weight_kg")
        private val HEIGHT_CM = intPreferencesKey("user_height_cm")
        private val AGE = intPreferencesKey("user_age")
        private val MONTHLY_UPDATE_ENABLED = booleanPreferencesKey("monthly_update_enabled")
        private val PREFERRED_WORKOUT_TYPES = stringPreferencesKey("preferred_workout_types")
        private val DIETARY_RESTRICTIONS = stringPreferencesKey("dietary_restrictions")
        private val FITNESS_GOAL = stringPreferencesKey("fitness_goal")
        private val FITNESS_LEVEL = stringPreferencesKey("fitness_level")
        private val USER_NOTES = stringPreferencesKey("user_notes")
        private val WORKOUT_SCHEDULE = stringPreferencesKey("workout_schedule")
    }

    val profileFlow: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            sex = prefs[SEX],
            weightKg = prefs[WEIGHT_KG],
            heightCm = prefs[HEIGHT_CM],
            age = prefs[AGE],
            monthlyUpdateEnabled = prefs[MONTHLY_UPDATE_ENABLED] ?: false,
            preferredWorkoutTypes = prefs[PREFERRED_WORKOUT_TYPES]?.fromJsonList() ?: emptyList(),
            dietaryRestrictions = prefs[DIETARY_RESTRICTIONS]?.fromJsonList() ?: emptyList(),
            fitnessGoal = prefs[FITNESS_GOAL],
            fitnessLevel = prefs[FITNESS_LEVEL],
            userNotes = prefs[USER_NOTES]?.fromJsonNotes() ?: emptyList(),
            workoutSchedule = prefs[WORKOUT_SCHEDULE]?.fromJsonSchedule() ?: emptyList()
        )
    }

    suspend fun saveProfile(
        weightKg: Float?,
        heightCm: Int?,
        age: Int?,
        monthlyUpdateEnabled: Boolean
    ) {
        dataStore.edit { prefs ->
            if (weightKg != null) prefs[WEIGHT_KG] = weightKg else prefs.remove(WEIGHT_KG)
            if (heightCm != null) prefs[HEIGHT_CM] = heightCm else prefs.remove(HEIGHT_CM)
            if (age != null) prefs[AGE] = age else prefs.remove(AGE)
            prefs[MONTHLY_UPDATE_ENABLED] = monthlyUpdateEnabled
        }
    }

    /** Chiamato dall'AI per aggiornare i dati fisici e il livello di forma. */
    suspend fun savePhysicalProfile(
        sex: String? = null,
        weightKg: Float? = null,
        heightCm: Int? = null,
        age: Int? = null,
        fitnessLevel: String? = null
    ) {
        dataStore.edit { prefs ->
            if (sex != null) prefs[SEX] = sex
            if (weightKg != null) prefs[WEIGHT_KG] = weightKg
            if (heightCm != null) prefs[HEIGHT_CM] = heightCm
            if (age != null) prefs[AGE] = age
            if (fitnessLevel != null) prefs[FITNESS_LEVEL] = fitnessLevel
        }
    }

    /** Chiamato dall'AI quando rileva preferenze nella conversazione. */
    suspend fun savePreferences(
        workoutTypes: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        fitnessGoal: String? = null,
        fitnessLevel: String? = null
    ) {
        dataStore.edit { prefs ->
            if (workoutTypes != null) prefs[PREFERRED_WORKOUT_TYPES] = workoutTypes.toJson()
            if (dietaryRestrictions != null) prefs[DIETARY_RESTRICTIONS] = dietaryRestrictions.toJson()
            if (fitnessGoal != null) prefs[FITNESS_GOAL] = fitnessGoal
            if (fitnessLevel != null) prefs[FITNESS_LEVEL] = fitnessLevel
        }
    }

    suspend fun getProfile(): UserProfile = profileFlow.first()

    suspend fun isProfileComplete(): Boolean {
        val profile = dataStore.data.first()
        return profile[SEX] != null &&
                profile[WEIGHT_KG] != null &&
                profile[HEIGHT_CM] != null &&
                profile[AGE] != null &&
                profile[FITNESS_LEVEL] != null
    }

    suspend fun isMonthlyUpdateEnabled(): Boolean =
        dataStore.data.first()[MONTHLY_UPDATE_ENABLED] ?: false

    suspend fun addNote(content: String) {
        val notes = getProfile().userNotes.toMutableList()
        notes.add(UserNote(content = content))
        dataStore.edit { prefs -> prefs[USER_NOTES] = gson.toJson(notes) }
    }

    suspend fun updateNote(id: String, content: String) {
        val notes = getProfile().userNotes.map { if (it.id == id) it.copy(content = content) else it }
        dataStore.edit { prefs -> prefs[USER_NOTES] = gson.toJson(notes) }
    }

    suspend fun deleteNote(id: String) {
        val notes = getProfile().userNotes.filter { it.id != id }
        dataStore.edit { prefs -> prefs[USER_NOTES] = gson.toJson(notes) }
    }

    suspend fun setNotes(notes: List<UserNote>) {
        dataStore.edit { prefs -> prefs[USER_NOTES] = gson.toJson(notes) }
    }

    suspend fun saveWorkoutSchedule(slots: List<WorkoutScheduleSlot>) {
        dataStore.edit { prefs -> prefs[WORKOUT_SCHEDULE] = gson.toJson(slots) }
    }

    private fun List<String>.toJson(): String = gson.toJson(this)
    private fun String.fromJsonList(): List<String> =
        gson.fromJson(this, object : TypeToken<List<String>>() {}.type) ?: emptyList()
    private fun String.fromJsonNotes(): List<UserNote> =
        gson.fromJson(this, object : TypeToken<List<UserNote>>() {}.type) ?: emptyList()
    private fun String.fromJsonSchedule(): List<WorkoutScheduleSlot> =
        gson.fromJson(this, object : TypeToken<List<WorkoutScheduleSlot>>() {}.type) ?: emptyList()
}

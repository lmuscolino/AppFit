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
import javax.inject.Inject
import javax.inject.Singleton

data class UserProfile(
    val weightKg: Float?,
    val heightCm: Int?,
    val age: Int?,
    val monthlyUpdateEnabled: Boolean,
    val preferredWorkoutTypes: List<String> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val fitnessGoal: String? = null
)

@Singleton
class UserProfileProvider @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val gson = Gson()

    companion object {
        private val WEIGHT_KG = floatPreferencesKey("user_weight_kg")
        private val HEIGHT_CM = intPreferencesKey("user_height_cm")
        private val AGE = intPreferencesKey("user_age")
        private val MONTHLY_UPDATE_ENABLED = booleanPreferencesKey("monthly_update_enabled")
        private val PREFERRED_WORKOUT_TYPES = stringPreferencesKey("preferred_workout_types")
        private val DIETARY_RESTRICTIONS = stringPreferencesKey("dietary_restrictions")
        private val FITNESS_GOAL = stringPreferencesKey("fitness_goal")
    }

    val profileFlow: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            weightKg = prefs[WEIGHT_KG],
            heightCm = prefs[HEIGHT_CM],
            age = prefs[AGE],
            monthlyUpdateEnabled = prefs[MONTHLY_UPDATE_ENABLED] ?: false,
            preferredWorkoutTypes = prefs[PREFERRED_WORKOUT_TYPES]?.fromJsonList() ?: emptyList(),
            dietaryRestrictions = prefs[DIETARY_RESTRICTIONS]?.fromJsonList() ?: emptyList(),
            fitnessGoal = prefs[FITNESS_GOAL]
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

    /** Chiamato dall'AI quando rileva preferenze nella conversazione. */
    suspend fun savePreferences(
        workoutTypes: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        fitnessGoal: String? = null
    ) {
        dataStore.edit { prefs ->
            if (workoutTypes != null) prefs[PREFERRED_WORKOUT_TYPES] = workoutTypes.toJson()
            if (dietaryRestrictions != null) prefs[DIETARY_RESTRICTIONS] = dietaryRestrictions.toJson()
            if (fitnessGoal != null) prefs[FITNESS_GOAL] = fitnessGoal
        }
    }

    suspend fun getProfile(): UserProfile = profileFlow.first()

    suspend fun isProfileComplete(): Boolean {
        val profile = dataStore.data.first()
        return profile[WEIGHT_KG] != null && profile[HEIGHT_CM] != null && profile[AGE] != null
    }

    suspend fun isMonthlyUpdateEnabled(): Boolean =
        dataStore.data.first()[MONTHLY_UPDATE_ENABLED] ?: false

    private fun List<String>.toJson(): String = gson.toJson(this)
    private fun String.fromJsonList(): List<String> =
        gson.fromJson(this, object : TypeToken<List<String>>() {}.type) ?: emptyList()
}

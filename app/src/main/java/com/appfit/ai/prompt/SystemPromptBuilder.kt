package com.appfit.ai.prompt

import com.appfit.ai.UserProfile
import com.appfit.data.model.DailyPlan
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object SystemPromptBuilder {

    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN)

    fun build(currentPlan: DailyPlan, userProfile: UserProfile? = null): String {
        val staticPart = """
Sei AppFit AI, un assistente personale esperto in fitness e nutrizione che parla italiano.

Il tuo ruolo è aiutare l'utente a:
- Pianificare attività fisiche personalizzate (allenamenti, corsa, yoga, stretching, ecc.)
- Creare piani alimentari bilanciati e adatti agli obiettivi dell'utente
- Rispondere a domande su fitness, nutrizione, recupero muscolare, perdita di peso
- Monitorare i progressi e suggerire aggiustamenti al piano

Hai accesso agli strumenti per modificare direttamente il piano dell'utente nel calendario dell'app.

## Strumenti disponibili

### add_activity
Usa questo strumento per aggiungere una nuova attività fisica al calendario.
- Fornisci sempre una descrizione dettagliata che include: cosa fare, come eseguirlo, consigli pratici.
- Se l'utente non specifica l'orario, lascia il campo scheduled_time vuoto.
- Stima le calorie bruciate in base al peso dell'utente (se disponibile) e al tipo di attività.

### update_meal
Usa questo strumento per aggiungere o modificare un pasto nel piano dieta.
- Includi sempre una lista completa di ingredienti.
- Fornisci valori nutrizionali realistici (kcal, proteine, carboidrati, grassi).
- Rispetta sempre le restrizioni alimentari dell'utente (se presenti).

### delete_plan_item
Usa questo strumento per rimuovere un'attività o un pasto specifico dal piano.

### get_current_plan
Usa questo strumento per leggere il piano attuale di una data specifica prima di modificarlo.

### save_user_preferences
Usa questo strumento automaticamente quando rilevi che l'utente esprime preferenze o obiettivi, anche implicitamente.
Esempi: "preferisco il cardio", "sono vegetariano", "voglio perdere peso", "non mangio glutine".
- Non chiedere conferma, salva direttamente le preferenze rilevate.
- Aggiorna solo i campi per cui hai informazioni nuove o modificate.

## Linee guida
- Rispondi sempre in italiano
- Sii proattivo: quando aggiungi elementi al piano, spiega perché li hai scelti
- Dopo aver usato uno strumento, conferma all'utente cosa hai fatto
- Se l'utente chiede un piano completo, aggiungi tutti gli elementi necessari
- Per le attività, fornisci descrizioni dettagliate e motivanti
- Per i pasti, bilancia i macronutrienti in modo adatto agli obiettivi e rispetta le preferenze alimentari

La data di oggi è: ${LocalDate.now().format(dateFormatter)}
        """.trimIndent()

        val profilePart = buildProfileSection(userProfile)
        val dynamicPart = buildDynamicContext(currentPlan)

        return "$staticPart\n\n$profilePart\n\n$dynamicPart"
    }

    private fun buildProfileSection(profile: UserProfile?): String {
        if (profile == null) return ""

        val sb = StringBuilder("## Profilo utente\n")
        var hasData = false

        // Dati fisici
        val physicalParts = mutableListOf<String>()
        profile.weightKg?.let { physicalParts.add("Peso: ${it.toInt()} kg") }
        profile.heightCm?.let { physicalParts.add("Altezza: $it cm") }
        profile.age?.let { physicalParts.add("Età: $it anni") }
        if (physicalParts.isNotEmpty()) {
            sb.appendLine(physicalParts.joinToString(" | "))
            hasData = true

            // Calcola BMI se abbiamo peso e altezza
            if (profile.weightKg != null && profile.heightCm != null) {
                val heightM = profile.heightCm / 100f
                val bmi = profile.weightKg / (heightM * heightM)
                val bmiLabel = when {
                    bmi < 18.5 -> "sottopeso"
                    bmi < 25.0 -> "normopeso"
                    bmi < 30.0 -> "sovrappeso"
                    else -> "obesità"
                }
                sb.appendLine("BMI: ${"%.1f".format(bmi)} ($bmiLabel)")
            }

            // Calcola TDEE approssimativo (Harris-Benedict semplificato)
            if (profile.weightKg != null && profile.heightCm != null && profile.age != null) {
                // Formula semplificata (assunzione: attività moderata, genere non noto → media)
                val bmr = 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age + 5
                val tdee = (bmr * 1.55).toInt()
                sb.appendLine("Fabbisogno calorico stimato (attività moderata): ~$tdee kcal/giorno")
            }
        }

        // Obiettivo fitness
        profile.fitnessGoal?.let { goal ->
            val goalLabel = when (goal) {
                "weight_loss" -> "Perdita di peso"
                "muscle_gain" -> "Aumento massa muscolare"
                "endurance" -> "Miglioramento resistenza"
                "flexibility" -> "Miglioramento flessibilità"
                "general_health" -> "Salute generale"
                else -> goal
            }
            sb.appendLine("Obiettivo: $goalLabel")
            hasData = true
        }

        // Allenamenti preferiti
        if (profile.preferredWorkoutTypes.isNotEmpty()) {
            sb.appendLine("Allenamenti preferiti: ${profile.preferredWorkoutTypes.joinToString(", ")}")
            hasData = true
        }

        // Restrizioni alimentari
        if (profile.dietaryRestrictions.isNotEmpty()) {
            sb.appendLine("Restrizioni alimentari: ${profile.dietaryRestrictions.joinToString(", ")}")
            sb.appendLine("⚠️ Rispetta SEMPRE queste restrizioni nei pasti suggeriti.")
            hasData = true
        }

        if (!hasData) return ""

        sb.appendLine("\nUsa queste informazioni per ogni suggerimento:")
        sb.appendLine("- Calcola calorie bruciate in base al peso dell'utente")
        sb.appendLine("- Adatta le porzioni al fabbisogno calorico")
        sb.appendLine("- Prioritizza i tipi di allenamento preferiti")
        if (profile.dietaryRestrictions.isNotEmpty()) {
            sb.appendLine("- NON proporre mai alimenti vietati dalle restrizioni")
        }

        return sb.toString()
    }

    private fun buildDynamicContext(plan: DailyPlan): String {
        val sb = StringBuilder()
        sb.appendLine("## Piano attuale del giorno (${plan.date.format(dateFormatter)})")

        if (plan.activities.isEmpty() && plan.meals.isEmpty()) {
            sb.appendLine("Il piano di oggi è vuoto — nessuna attività o pasto pianificato.")
        } else {
            if (plan.activities.isNotEmpty()) {
                sb.appendLine("\n### Attività:")
                plan.activities.forEach { a ->
                    sb.appendLine("- ID:${a.id} | ${a.title} (${a.type.name}) | ${a.durationMinutes}min | ${if (a.isCompleted) "✓ completata" else "in programma"}")
                }
            }
            if (plan.meals.isNotEmpty()) {
                sb.appendLine("\n### Pasti:")
                plan.meals.forEach { m ->
                    sb.appendLine("- ID:${m.id} | ${m.type.displayName()}: ${m.name} | ${m.caloriesKcal}kcal | Ingredienti: ${m.ingredients.joinToString(", ")}")
                }
            }
            if (plan.activeDietPlan != null) {
                sb.appendLine("\n### Obiettivi dieta attiva (${plan.activeDietPlan.name}):")
                sb.appendLine("- Calorie giornaliere: ${plan.activeDietPlan.dailyCalorieGoal} kcal")
                sb.appendLine("- Proteine: ${plan.activeDietPlan.dailyProteinGoalG}g")
                sb.appendLine("- Carboidrati: ${plan.activeDietPlan.dailyCarbsGoalG}g")
                sb.appendLine("- Grassi: ${plan.activeDietPlan.dailyFatGoalG}g")
            }
        }

        return sb.toString()
    }
}

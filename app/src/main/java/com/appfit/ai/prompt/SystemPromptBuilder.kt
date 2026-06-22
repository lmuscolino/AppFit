package com.appfit.ai.prompt

import com.appfit.ai.UserProfile
import com.appfit.ai.WorkoutScheduleSlot
import com.appfit.data.model.DailyPlan
import com.appfit.data.model.FavoriteRecipe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object SystemPromptBuilder {

    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN)

    fun build(currentPlan: DailyPlan, userProfile: UserProfile? = null, favoriteRecipes: List<FavoriteRecipe> = emptyList()): String {
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

### update_activity
Usa questo strumento per modificare un'attività già esistente nel calendario.
- Richiede activity_id: usa prima get_current_plan per ottenere gli ID delle attività.
- Aggiorna solo i campi che l'utente vuole modificare; gli altri restano invariati.
- Sincronizza automaticamente l'evento su Google Calendar.

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

### update_user_notes
Usa questo strumento automaticamente quando l'utente condivide informazioni personali rilevanti che non rientrano nelle preferenze standard.
Esempi: "ho un'ernia al disco", "sono in gravidanza", "ho un ginocchio operato".
- Azione "add": aggiunge una nuova nota
- Azione "update": modifica una nota esistente (fornire l'id)
- Azione "delete": elimina una nota specifica (fornire l'id)
- Azione "clear": elimina tutte le note
- Non chiedere conferma, salva/aggiorna direttamente. Informa l'utente di cosa hai fatto.

### save_workout_schedule
Usa questo strumento automaticamente quando l'utente indica i giorni e/o le fasce orarie in cui preferisce allenarsi.
Esempi: "mi alleno lunedì, mercoledì e venerdì mattina dalle 7 alle 9", "preferisco allenarmi la sera dopo le 18", "sono libero solo il weekend".
- Passa una lista di slot, ciascuno con i giorni (MONDAY…SUNDAY) e gli orari (HH:mm).
- Sostituisce completamente la schedule precedente.
- Non chiedere conferma. Informa l'utente della schedule salvata.

## Prima di generare piani

I campi OBBLIGATORI per qualsiasi piano sono: **sesso, peso, altezza, età**.
Per i piani di allenamento è obbligatorio anche il **livello di forma fisica**.

**Procedura da seguire sempre:**
1. Controlla il profilo utente qui sotto.
2. Se manca anche solo uno dei campi obbligatori, elenca TUTTI i campi mancanti in un unico messaggio e chiedi all'utente di fornirli prima di procedere. Non generare nulla finché non li hai tutti.
3. Appena l'utente risponde con i dati, chiama IMMEDIATAMENTE `update_profile_data` per salvarli nel profilo, poi genera il piano.

Adatta sempre l'intensità al livello di forma fisica:
- Principiante: esercizi base, serie brevi (2-3), recuperi lunghi (90-120s), carichi leggeri
- Intermedio: volume moderato, 3-4 serie, recuperi medi (60-90s)
- Avanzato: volume alto, 4-5 serie, tecniche avanzate, recuperi brevi (30-60s)

## Linee guida
- Rispondi sempre in italiano
- Sii proattivo: quando aggiungi elementi al piano, spiega perché li hai scelti
- Dopo aver usato uno strumento, conferma all'utente cosa hai fatto
- Se l'utente chiede un piano completo, aggiungi tutti gli elementi necessari
- Per le attività, fornisci descrizioni dettagliate e motivanti
- Per i pasti, bilancia i macronutrienti in modo adatto agli obiettivi e rispetta le preferenze alimentari
- Quando crei piani dietetici, privilegia ricette con pochi ingredienti (max 6-8 per pasto) e riutilizza gli stessi ingredienti in più pasti per minimizzare la lista della spesa. Crea ricette complesse solo se l'utente lo richiede esplicitamente.
- Esegui sempre le azioni direttamente con gli strumenti, senza prima annunciare le intenzioni. Non scrivere frasi come "ecco cosa farò" o "mi appresto a creare": chiama immediatamente i tool necessari, poi conferma all'utente cosa hai fatto.
- Quando crei piani multi-giorno o completi, aggiungi tutti gli elementi in una singola sessione senza fermarti dopo il primo: continua ad usare gli strumenti fino al completamento dell'intero piano.

La data di oggi è: ${LocalDate.now().format(dateFormatter)}
        """.trimIndent()

        val profilePart = buildProfileSection(userProfile)
        val favoritesPart = buildFavoritesSection(favoriteRecipes)
        val dynamicPart = buildDynamicContext(currentPlan)

        return "$staticPart\n\n$profilePart\n\n$favoritesPart\n\n$dynamicPart"
    }

    private fun buildProfileSection(profile: UserProfile?): String {
        if (profile == null) return ""

        val sb = StringBuilder("## Profilo utente\n")
        var hasData = false

        // Dati fisici
        val physicalParts = mutableListOf<String>()
        profile.sex?.let { physicalParts.add("Sesso: ${if (it == "male") "Maschio" else "Femmina"}") }
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

            // Calcola TDEE con Harris-Benedict (usa sesso se disponibile)
            if (profile.weightKg != null && profile.heightCm != null && profile.age != null) {
                val bmr = when (profile.sex) {
                    "male" -> 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age + 5
                    "female" -> 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age - 161
                    else -> 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age - 78 // media M/F
                }
                val tdee = (bmr * 1.55).toInt()
                sb.appendLine("Fabbisogno calorico stimato (attività moderata): ~$tdee kcal/giorno")
            }
        }

        // Warning campi obbligatori mancanti
        val missingMandatory = buildList {
            if (profile.sex == null) add("sesso")
            if (profile.weightKg == null) add("peso")
            if (profile.heightCm == null) add("altezza")
            if (profile.age == null) add("età")
            if (profile.fitnessLevel == null) add("livello di forma fisica")
        }
        if (missingMandatory.isNotEmpty()) {
            sb.appendLine("⛔ PROFILO INCOMPLETO — campi obbligatori mancanti: ${missingMandatory.joinToString(", ")}")
            sb.appendLine("→ Prima di creare qualsiasi piano, chiedi ALL'UTENTE tutti questi campi in un UNICO messaggio. Non generare nulla finché non li ricevi.")
            hasData = true
        }

        // Livello di forma fisica
        profile.fitnessLevel?.let { level ->
            val levelLabel = when (level) {
                "beginner" -> "Principiante (si allena raramente o è agli inizi)"
                "intermediate" -> "Intermedio (si allena regolarmente da qualche mese)"
                "advanced" -> "Avanzato (si allena intensamente da anni)"
                else -> level
            }
            sb.appendLine("Livello di forma fisica: $levelLabel")
            hasData = true
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

        // Fasce orarie di allenamento
        if (profile.workoutSchedule.isNotEmpty()) {
            sb.appendLine("Fasce orarie di allenamento preferite:")
            profile.workoutSchedule.forEach { slot ->
                val dayNames = slot.days
                    .mapNotNull { runCatching { DayOfWeek.valueOf(it) }.getOrNull() }
                    .sortedBy { it.value }
                    .joinToString(", ") { it.getDisplayName(java.time.format.TextStyle.FULL, Locale.ITALIAN) }
                val timeRange = when {
                    slot.startTime.isNotBlank() && slot.endTime.isNotBlank() -> " dalle ${slot.startTime} alle ${slot.endTime}"
                    slot.startTime.isNotBlank() -> " dalle ${slot.startTime}"
                    else -> ""
                }
                sb.appendLine("  - $dayNames$timeRange")
            }
            sb.appendLine("⚠️ Quando aggiungi attività, rispetta queste fasce orarie. Pianifica gli allenamenti in questi giorni e orari, salvo indicazione contraria dell'utente.")
            hasData = true
        }

        // Note personali
        if (profile.userNotes.isNotEmpty()) {
            sb.appendLine("Note personali:")
            profile.userNotes.forEach { note ->
                sb.appendLine("  - [ID:${note.id}] ${note.content}")
            }
            sb.appendLine("⚠️ Tieni conto di queste note in ogni consiglio. Quando aggiorni una nota usa il suo ID.")
            hasData = true
        }

        if (!hasData) return ""

        sb.appendLine("\nUsa queste informazioni per ogni suggerimento:")
        sb.appendLine("- Calcola calorie bruciate in base al peso dell'utente")
        sb.appendLine("- Adatta le porzioni al fabbisogno calorico")
        sb.appendLine("- Prioritizza i tipi di allenamento preferiti")
        if (profile.fitnessLevel != null) {
            sb.appendLine("- Adatta l'intensità degli allenamenti al livello di forma fisica dichiarato")
        }
        if (profile.dietaryRestrictions.isNotEmpty()) {
            sb.appendLine("- NON proporre mai alimenti vietati dalle restrizioni")
        }

        return sb.toString()
    }

    private fun buildFavoritesSection(favorites: List<FavoriteRecipe>): String {
        if (favorites.isEmpty()) return ""
        val sb = StringBuilder("## Ricette preferite dell'utente\n")
        sb.appendLine("Quando pianifichi i pasti, dai priorità a queste ricette salvate dall'utente:")
        favorites.forEach { r ->
            sb.appendLine("- ${r.name} (${r.mealType.displayName()}, ${r.caloriesKcal} kcal | P:${r.proteinG}g C:${r.carbsG}g G:${r.fatG}g)")
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

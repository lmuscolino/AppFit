package com.appfit.domain.usecase

import com.appfit.data.model.ShoppingCategory
import com.appfit.data.model.ShoppingItem
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class GenerateShoppingListUseCase @Inject constructor(
    private val dietRepository: DietRepository,
    private val shoppingRepository: ShoppingRepository
) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate) {
        val meals = dietRepository.getMealsForRange(start, end).first()

        // Aggregate all ingredients from all meals
        val aggregated = mutableMapOf<String, MutableList<String>>() // normalized name -> list of raw entries (List preserves duplicates for quantity summing)

        meals.forEach { meal ->
            meal.ingredients.forEach { rawIngredient ->
                if (rawIngredient.isNotBlank()) {
                    val normalized = normalizeIngredient(rawIngredient)
                    aggregated.getOrPut(normalized) { mutableListOf() }.add(rawIngredient)
                }
            }
        }

        // Merge keys where one is a full-word prefix of another (e.g. "basilico" + "basilico fresco" → "basilico")
        val sortedKeys = aggregated.keys.sortedBy { it.length }.toList()
        for (shorter in sortedKeys) {
            if (!aggregated.containsKey(shorter)) continue
            for (longer in sortedKeys) {
                if (longer == shorter || !aggregated.containsKey(longer)) continue
                if (longer.startsWith("$shorter ")) {
                    aggregated.getOrPut(shorter) { mutableListOf() }.addAll(aggregated.remove(longer)!!)
                }
            }
        }

        val shoppingItems = aggregated.entries.map { (normalized, rawEntries) ->
            val (summedQty, unit) = sumQuantities(rawEntries)
            ShoppingItem(
                name = normalized.replaceFirstChar { it.uppercase() },
                quantity = summedQty,
                unit = unit,
                category = classifyIngredient(normalized),
                weekStartDate = start
            )
        }.sortedWith(compareBy({ it.category.order() }, { it.name }))

        shoppingRepository.replaceShoppingListForWeek(start, shoppingItems)
    }

    private fun sumQuantities(rawEntries: Collection<String>): Pair<String, String> {
        val parsed = rawEntries.mapNotNull { raw ->
            val qtyStr = extractQuantity(raw)
            val qty = qtyStr.replace(',', '.').toFloatOrNull()
            val unit = extractUnit(raw)
            if (qty != null) Pair(qty, unit) else null
        }
        if (parsed.isEmpty()) return Pair("", "")

        val units = parsed.map { it.second }.toSet()
        return if (units.size == 1) {
            // Same unit: sum numerically
            val total = parsed.sumOf { it.first.toDouble() }.toFloat()
            val totalStr = if (total == total.toLong().toFloat()) total.toLong().toString() else "%.1f".format(total)
            Pair(totalStr, units.first())
        } else {
            // Mixed or no units: concatenate raw strings
            Pair(rawEntries.distinct().joinToString(" + "), "")
        }
    }

    private fun normalizeIngredient(raw: String): String {
        val unitPattern = "g|gr|kg|ml|l|cl|oz|lb|tsp|tbsp|cup|pz|pcs|fette|fetta"
        val withoutQty = raw
            // Rimuove ", 150g" o " 150g" o " (150g)" ovunque nel testo
            .replace(Regex("[,(\\s]+\\d+[.,]?\\d*\\s*($unitPattern)\\b[)]*", RegexOption.IGNORE_CASE), "")
            // Rimuove quantità all'inizio "150g nome" o "150 nome"
            .replace(Regex("^\\d+[.,]?\\d*\\s*($unitPattern)?\\s*", RegexOption.IGNORE_CASE), "")
            // Rimuove parentesi vuote residue e di (quantità)
            .replace(Regex("\\(\\s*\\)"), "")
            .replace(Regex("\\(\\d+[.,]?\\d*\\s*($unitPattern)?\\s*\\)", RegexOption.IGNORE_CASE), "")
            .trim().trimEnd(',', ';', ')').trim()
            .lowercase()
        return if (withoutQty.length >= 3) withoutQty else raw.lowercase().trim()
    }

    private fun extractQuantity(raw: String): String {
        val unitPattern = "g|gr|kg|ml|l|cl|oz|lb|tsp|tbsp|cup|pz|pcs|fette|fetta"
        // Cerca "150g" o "150 g" ovunque nella stringa (con unità)
        val withUnit = Regex("(\\d+[.,]?\\d*)\\s*($unitPattern)\\b", RegexOption.IGNORE_CASE).find(raw)
        if (withUnit != null) return withUnit.groupValues[1]
        // Fallback: numero senza unità all'inizio
        return Regex("^(\\d+[.,]?\\d*)").find(raw.trim())?.groupValues?.get(1) ?: ""
    }

    private fun extractUnit(raw: String): String {
        val unitPattern = "g|gr|kg|ml|l|cl|oz|lb|tsp|tbsp|cup|pz|pcs|fette|fetta"
        val match = Regex("\\d+[.,]?\\d*\\s*($unitPattern)\\b", RegexOption.IGNORE_CASE).find(raw)
        return match?.groupValues?.get(1)?.lowercase() ?: ""
    }

    private fun classifyIngredient(normalized: String): ShoppingCategory {
        val n = normalized.lowercase()
        return when {
            n.containsAny("insalata","lattuga","spinaci","rucola","pomodoro","carota","cipolla","aglio","peperone",
                "zucchina","melanzana","broccoli","cavolfiore","patata","funghi","porro","sedano","finocchio",
                "mela","pera","banana","arancia","limone","fragola","uva","pesca","kiwi","avocado","frutta","verdura") -> ShoppingCategory.PRODUCE

            n.containsAny("pollo","petto di pollo","tacchino","manzo","maiale","salmone","tonno","uova","uova","prosciutto",
                "carne","pesce","gamberi","polpo","merluzzo","legumi","fagioli","ceci","lenticchie","tofu","tempeh",
                "proteina","whey") -> ShoppingCategory.PROTEIN

            n.containsAny("latte","yogurt","formaggio","mozzarella","parmigiano","ricotta","burro","panna","kefir",
                "grana") -> ShoppingCategory.DAIRY

            n.containsAny("pasta","riso","pane","farro","orzo","quinoa","avena","farina","cereali","cracker","crackers",
                "grissini","polenta","couscous","bulgur") -> ShoppingCategory.GRAINS

            n.containsAny("olio","sale","pepe","spezie","erbe","aceto","salsa","ketchup","maionese","senape","dado",
                "brodo","conserva","passata","pelati","tonno in scatola","legumi in scatola","fagioli in scatola",
                "ceci in scatola","lenticchie in scatola","miele","marmellata","cioccolato","cacao","zucchero") -> ShoppingCategory.PANTRY

            else -> ShoppingCategory.OTHER
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it) }
}

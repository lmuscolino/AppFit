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
        val aggregated = mutableMapOf<String, MutableSet<String>>() // normalized name -> set of raw entries

        meals.forEach { meal ->
            meal.ingredients.forEach { rawIngredient ->
                if (rawIngredient.isNotBlank()) {
                    val normalized = normalizeIngredient(rawIngredient)
                    aggregated.getOrPut(normalized) { mutableSetOf() }.add(rawIngredient)
                }
            }
        }

        val shoppingItems = aggregated.entries.map { (normalized, rawEntries) ->
            ShoppingItem(
                name = normalized.replaceFirstChar { it.uppercase() },
                quantity = extractQuantity(rawEntries.first()),
                unit = extractUnit(rawEntries.first()),
                category = classifyIngredient(normalized),
                weekStartDate = start  // usa start come chiave di storage
            )
        }.sortedWith(compareBy({ it.category.order() }, { it.name }))

        shoppingRepository.replaceShoppingListForWeek(start, shoppingItems)
    }

    private fun normalizeIngredient(raw: String): String {
        // Remove quantities and units, lowercase
        val withoutQuantity = raw
            .replace(Regex("^\\d+[.,]?\\d*\\s*(g|gr|kg|ml|l|cl|oz|lb|tsp|tbsp|cup|pz|pcs|fette|fetta)?\\s*"), "")
            .trim()
            .lowercase()
        return if (withoutQuantity.length >= 3) withoutQuantity else raw.lowercase().trim()
    }

    private fun extractQuantity(raw: String): String {
        val match = Regex("^(\\d+[.,]?\\d*)").find(raw.trim())
        return match?.value ?: ""
    }

    private fun extractUnit(raw: String): String {
        val match = Regex("^\\d+[.,]?\\d*\\s*(g|gr|kg|ml|l|cl|oz|lb|tsp|tbsp|cup|pz|pcs|fette|fetta)\\b").find(raw.trim())
        return match?.groupValues?.get(1) ?: ""
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

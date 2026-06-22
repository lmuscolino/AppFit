package com.appfit.ui.diet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.DailyPlan
import com.appfit.data.model.Meal
import com.appfit.data.model.MealType
import com.appfit.ui.common.UiState
import com.appfit.ui.theme.GradientTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(
    onOpenDrawer: () -> Unit = {},
    onMealClick: (Long) -> Unit = {},
    viewModel: DietViewModel = hiltViewModel()
) {
    val state by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    val isToday = selectedDate == today
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.ITALIAN)

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = {
                    Column {
                        Text(
                            if (isToday) "Dieta di oggi" else "Piano dieta",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            selectedDate.format(dateFormatter),
                            style = MaterialTheme.typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::prevDay) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Giorno precedente")
                    }
                    if (!isToday) {
                        IconButton(onClick = viewModel::goToToday) {
                            Icon(Icons.Filled.Today, contentDescription = "Oggi")
                        }
                    }
                    IconButton(onClick = viewModel::nextDay) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Giorno successivo")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
            is UiState.Success -> {
                val plan = s.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Macro summary
                    item {
                        MacroSummaryCard(plan)
                    }

                    // Meals grouped by type
                    val mealsByType = plan.meals.groupBy { it.type }
                    MealType.values().forEach { type ->
                        val meals = mealsByType[type]
                        if (!meals.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = type.displayName(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            items(meals, key = { it.id }) { meal ->
                                MealDetailCard(meal = meal, onClick = { onMealClick(meal.id) })
                            }
                        }
                    }

                    if (plan.meals.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Filled.Restaurant,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Nessun pasto pianificato per ${if (isToday) "oggi" else "questo giorno"}.\nChiedi all'AI di creare un piano dieta!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MacroSummaryCard(plan: DailyPlan) {
    val goal = plan.activeDietPlan

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Riepilogo nutrizionale",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Calorie progress
            val calorieGoal = goal?.dailyCalorieGoal ?: 2000
            val caloriesConsumed = plan.totalCaloriesConsumed
            val progress = (caloriesConsumed.toFloat() / calorieGoal).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$caloriesConsumed kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Obiettivo: $calorieGoal kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Macros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroItem("Proteine", "${plan.totalProteinG}g", goal?.dailyProteinGoalG?.let { "${it}g" })
                MacroItem("Carboidrati", "${plan.totalCarbsG}g", goal?.dailyCarbsGoalG?.let { "${it}g" })
                MacroItem("Grassi", "${plan.totalFatG}g", goal?.dailyFatGoalG?.let { "${it}g" })
            }
        }
    }
}

@Composable
private fun MacroItem(name: String, value: String, goal: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
        if (goal != null) {
            Text(
                text = "/ $goal",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MealDetailCard(meal: Meal, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (meal.description.isNotBlank()) {
                        Text(
                            text = meal.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (meal.caloriesKcal > 0) {
                    Text(
                        text = "${meal.caloriesKcal} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (meal.ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Ingredienti:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                meal.ingredients.forEach { ingredient ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            text = "• $ingredient",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Macros row
            if (meal.proteinG > 0 || meal.carbsG > 0 || meal.fatG > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroChip("P: ${meal.proteinG}g")
                    MacroChip("C: ${meal.carbsG}g")
                    MacroChip("G: ${meal.fatG}g")
                }
            }
        }
    }
}

@Composable
private fun MacroChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

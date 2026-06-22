package com.appfit.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.appfit.data.model.FavoriteRecipe
import com.appfit.data.model.MealType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    var recipeForDatePicker by remember { mutableStateOf<FavoriteRecipe?>(null) }
    var addedRecipeId by remember { mutableStateOf<Long?>(null) }

    // Date picker dialog
    recipeForDatePicker?.let { recipe ->
        DatePickerDialog(
            recipe = recipe,
            onConfirm = { date ->
                viewModel.addToMealPlan(recipe, date)
                addedRecipeId = recipe.id
                recipeForDatePicker = null
            },
            onDismiss = { recipeForDatePicker = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ricette preferite") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = null,
                        modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Text("Nessuna ricetta salvata",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline)
                    Text("Apri il dettaglio di un pasto e tocca l'icona cuore per salvarlo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val grouped = favorites.groupBy { it.mealType }
                MealType.values().forEach { type ->
                    val group = grouped[type] ?: return@forEach
                    item {
                        Text(
                            type.displayName(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(group, key = { it.id }) { recipe ->
                        FavoriteRecipeCard(
                            recipe = recipe,
                            addedToday = addedRecipeId == recipe.id,
                            onAddToPlan = { recipeForDatePicker = recipe },
                            onDelete = { viewModel.deleteFavorite(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteRecipeCard(
    recipe: FavoriteRecipe,
    addedToday: Boolean,
    onAddToPlan: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(recipe.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${recipe.caloriesKcal} kcal · P:${recipe.proteinG}g C:${recipe.carbsG}g G:${recipe.fatG}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Riduci" else "Espandi"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Elimina dai preferiti",
                        tint = MaterialTheme.colorScheme.error)
                }
            }

            if (expanded && recipe.ingredients.isNotEmpty()) {
                HorizontalDivider()
                recipe.ingredients.forEach { ing ->
                    Text("• $ing", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Button(
                onClick = onAddToPlan,
                modifier = Modifier.fillMaxWidth(),
                colors = if (addedToday) ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) else ButtonDefaults.buttonColors()
            ) {
                Icon(
                    if (addedToday) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (addedToday) "Aggiunto al piano" else "Aggiungi al piano")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    recipe: FavoriteRecipe,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val date = LocalDate.ofEpochDay(millis / 86_400_000L)
                    onConfirm(date)
                }
            }) { Text("Aggiungi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text("Scegli la data per \"${recipe.name}\"",
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp))
            }
        )
    }
}

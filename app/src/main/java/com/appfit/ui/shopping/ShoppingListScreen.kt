package com.appfit.ui.shopping

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.ShoppingItem
import com.appfit.ui.theme.GradientTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    onOpenDrawer: () -> Unit = {},
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val groupedItems by viewModel.groupedItems.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val rangeStart by viewModel.rangeStart.collectAsStateWithLifecycle()
    val rangeEnd by viewModel.rangeEnd.collectAsStateWithLifecycle()

    val totalItems = groupedItems.values.sumOf { it.size }
    val checkedItems = groupedItems.values.sumOf { list -> list.count { it.isChecked } }

    // Date range picker state
    var showPickStart by remember { mutableStateOf(false) }
    var showPickEnd by remember { mutableStateOf(false) }
    var pendingStart by remember { mutableStateOf<LocalDate?>(null) }

    // Add/Edit dialog state
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ShoppingItem?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN)

    // First DatePickerDialog — selects start date
    if (showPickStart) {
        val startPickerState = rememberDatePickerState(
            initialSelectedDateMillis = rangeStart.toEpochDay() * 86400000L
        )
        DatePickerDialog(
            onDismissRequest = { showPickStart = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = startPickerState.selectedDateMillis
                    if (millis != null) {
                        pendingStart = LocalDate.ofEpochDay(millis / 86400000L)
                        showPickStart = false
                        showPickEnd = true
                    }
                }) { Text("Avanti") }
            },
            dismissButton = {
                TextButton(onClick = { showPickStart = false }) { Text("Annulla") }
            }
        ) {
            DatePicker(state = startPickerState, title = { Text("Data inizio", modifier = Modifier.padding(start = 24.dp, top = 16.dp)) })
        }
    }

    // Second DatePickerDialog — selects end date
    if (showPickEnd) {
        val endPickerState = rememberDatePickerState(
            initialSelectedDateMillis = rangeEnd.toEpochDay() * 86400000L
        )
        DatePickerDialog(
            onDismissRequest = { showPickEnd = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = endPickerState.selectedDateMillis
                    val start = pendingStart
                    if (millis != null && start != null) {
                        val end = LocalDate.ofEpochDay(millis / 86400000L)
                        viewModel.setCustomRange(start, end)
                    }
                    showPickEnd = false
                    pendingStart = null
                }) { Text("Conferma") }
            },
            dismissButton = {
                TextButton(onClick = { showPickEnd = false; pendingStart = null }) { Text("Annulla") }
            }
        ) {
            DatePicker(state = endPickerState, title = { Text("Data fine", modifier = Modifier.padding(start = 24.dp, top = 16.dp)) })
        }
    }

    // Add/Edit dialog
    if (showAddDialog || editingItem != null) {
        AddEditShoppingItemDialog(
            item = editingItem,
            onDismiss = { showAddDialog = false; editingItem = null },
            onSave = { name, qty, unit ->
                val existing = editingItem
                if (existing != null) {
                    viewModel.updateItem(existing, name, qty, unit)
                } else {
                    viewModel.addItem(name, qty, unit)
                }
                showAddDialog = false
                editingItem = null
            }
        )
    }

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = {
                    Column {
                        Text("Lista della spesa", style = MaterialTheme.typography.titleLarge)
                        if (totalItems > 0) {
                            Text(
                                "$checkedItems/$totalItems completati",
                                style = MaterialTheme.typography.bodySmall,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Aggiungi articolo")
                }
                ExtendedFloatingActionButton(
                    onClick = { viewModel.regenerateFromMealPlan() },
                    icon = {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Refresh, contentDescription = null)
                        }
                    },
                    text = { Text("Rigenera dalla dieta") },
                    expanded = !isGenerating
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Range navigation bar
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = viewModel::prevWeek) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Settimana precedente",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text(
                        text = "${rangeStart.format(dateFormatter)} – ${rangeEnd.format(dateFormatter)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(onClick = { showPickStart = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Range personalizzato",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    IconButton(onClick = viewModel::nextWeek) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Settimana successiva",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            if (groupedItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Text(
                            "Nessun articolo per questo periodo.\nAggiungi pasti nel piano dieta\ne premi Rigenera.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    groupedItems.forEach { (category, items) ->
                        stickyHeader(key = category.name) {
                            Surface(color = MaterialTheme.colorScheme.background) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (category) {
                                            com.appfit.data.model.ShoppingCategory.PRODUCE -> Icons.Filled.Eco
                                            com.appfit.data.model.ShoppingCategory.PROTEIN -> Icons.Filled.SetMeal
                                            com.appfit.data.model.ShoppingCategory.DAIRY -> Icons.Filled.LocalDrink
                                            com.appfit.data.model.ShoppingCategory.GRAINS -> Icons.Filled.Grain
                                            com.appfit.data.model.ShoppingCategory.PANTRY -> Icons.Filled.Kitchen
                                            com.appfit.data.model.ShoppingCategory.OTHER -> Icons.Filled.MoreHoriz
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = category.displayName(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${items.count { it.isChecked }}/${items.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                HorizontalDivider()
                            }
                        }

                        items(items, key = { it.id }) { item ->
                            ShoppingItemRow(
                                item = item,
                                onToggle = { viewModel.toggleItemChecked(item) },
                                onEdit = { editingItem = item },
                                onDelete = { viewModel.deleteItem(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEditShoppingItemDialog(
    item: ShoppingItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: String, unit: String) -> Unit
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var quantity by remember(item) { mutableStateOf(item?.quantity ?: "") }
    var unit by remember(item) { mutableStateOf(item?.unit ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item != null) "Modifica articolo" else "Aggiungi articolo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantità") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unità") },
                        singleLine = true,
                        placeholder = { Text("g, kg, pz…") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, quantity, unit) },
                enabled = name.isNotBlank()
            ) { Text("Salva") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.isChecked, onCheckedChange = { onToggle() })
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurface
            )
            if (item.quantity.isNotBlank() || item.unit.isNotBlank()) {
                Text(
                    text = "${item.quantity} ${item.unit}".trim(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Elimina",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

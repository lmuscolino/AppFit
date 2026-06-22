package com.appfit.ui.reminders

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.Reminder
import com.appfit.data.model.ReminderCategory
import com.appfit.ui.theme.GradientTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    onOpenDrawer: () -> Unit = {},
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val pending by viewModel.reminders.collectAsStateWithLifecycle()
    val all by viewModel.completedReminders.collectAsStateWithLifecycle()
    val completed = all.filter { it.isCompleted }

    var showAddDialog by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, cat, date, amount, important ->
                viewModel.addReminder(title, desc, cat, date, amount, important)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = { Text("Promemoria", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Aggiungi", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (pending.isEmpty() && completed.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", style = MaterialTheme.typography.displayMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Nessun promemoria",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Aggiungi scadenze o collegati a Gmail\nper rilevarle automaticamente",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Sezione: in attesa
            if (pending.isNotEmpty()) {
                item {
                    Text(
                        "In attesa",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(pending, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onToggle = { viewModel.toggleCompleted(reminder) },
                        onDelete = { viewModel.delete(reminder.id) }
                    )
                }
            }

            // Sezione: completati (collassabili)
            if (completed.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Completati (${completed.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showCompleted = !showCompleted }) {
                            Icon(
                                if (showCompleted) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }
                }
                if (showCompleted) {
                    items(completed, key = { "done_${it.id}" }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onToggle = { viewModel.toggleCompleted(reminder) },
                            onDelete = { viewModel.delete(reminder.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val today = LocalDate.now()
    val isOverdue = reminder.dueDate != null && reminder.dueDate.isBefore(today) && !reminder.isCompleted
    val isDueSoon = reminder.dueDate != null && !reminder.dueDate.isBefore(today) &&
            reminder.dueDate.isBefore(today.plusDays(7)) && !reminder.isCompleted
    var expanded by remember { mutableStateOf(false) }

    val containerColor = when {
        reminder.isCompleted -> MaterialTheme.colorScheme.surfaceVariant
        isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        reminder.isImportant -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(if (reminder.isImportant && !reminder.isCompleted) 3.dp else 1.dp),
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = if (expanded) Alignment.Top else Alignment.CenterVertically
        ) {
            // Check button
            IconButton(onClick = onToggle, modifier = Modifier.size(36.dp)) {
                Icon(
                    if (reminder.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (reminder.isCompleted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(8.dp))

            // Emoji categoria
            Text(reminder.category.emoji(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (reminder.isImportant && !reminder.isCompleted) {
                        Icon(
                            Icons.Filled.PriorityHigh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                    }
                    Text(
                        reminder.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (reminder.isImportant) FontWeight.SemiBold else FontWeight.Normal,
                        textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                }
                if (reminder.description.isNotBlank()) {
                    Text(
                        reminder.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        reminder.category.displayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    reminder.dueDate?.let { date ->
                        Text(" · ", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            formatDueDate(date, today),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                isOverdue -> MaterialTheme.colorScheme.error
                                isDueSoon -> Color(0xFFE65100)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isOverdue || isDueSoon) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                    reminder.amount?.let { amt ->
                        Text(" · ", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "€${"%.2f".format(amt)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Elimina",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatDueDate(date: LocalDate, today: LocalDate): String {
    val days = today.until(date).days
    return when {
        date.isBefore(today) -> "Scaduto ${-today.until(date).days}gg fa"
        date == today -> "Scade oggi"
        days == 1 -> "Scade domani"
        days <= 7 -> "Scade tra $days giorni"
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ITALIAN))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, ReminderCategory, LocalDate?, Float?, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ReminderCategory.OTHER) }
    var dueDateText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isImportant by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo promemoria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titolo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Text("Categoria", style = MaterialTheme.typography.labelMedium)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReminderCategory.entries.take(3).forEachIndexed { idx, cat ->
                        SegmentedButton(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            shape = SegmentedButtonDefaults.itemShape(idx, 3)
                        ) { Text(cat.emoji(), style = MaterialTheme.typography.labelSmall) }
                    }
                }
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReminderCategory.entries.drop(3).forEachIndexed { idx, cat ->
                        SegmentedButton(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            shape = SegmentedButtonDefaults.itemShape(idx, 3)
                        ) { Text(cat.emoji(), style = MaterialTheme.typography.labelSmall) }
                    }
                }
                Text(
                    "Categoria selezionata: ${selectedCategory.emoji()} ${selectedCategory.displayName()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = dueDateText,
                    onValueChange = { dueDateText = it },
                    label = { Text("Scadenza (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Importo (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isImportant, onCheckedChange = { isImportant = it })
                    Spacer(Modifier.width(4.dp))
                    Text("Importante (notifica ripetuta)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueDate = runCatching { LocalDate.parse(dueDateText) }.getOrNull()
                        val amount = amountText.toFloatOrNull()
                        onConfirm(title, description, selectedCategory, dueDate, amount, isImportant)
                    }
                },
                enabled = title.isNotBlank()
            ) { Text("Aggiungi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

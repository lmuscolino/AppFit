package com.appfit.ui.pendinginbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.PendingEmailItem
import com.appfit.data.model.PendingItemType
import com.appfit.ui.pendinginbox.PendingInboxViewModel.ScanState
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

private val italianDateFmt = DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.ITALIAN)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingInboxScreen(
    onOpenDrawer: () -> Unit,
    viewModel: PendingInboxViewModel = hiltViewModel()
) {
    val items by viewModel.pendingItems.collectAsStateWithLifecycle()
    val scanState by viewModel.scanState.collectAsStateWithLifecycle()

    LaunchedEffect(scanState) {
        if (scanState == ScanState.Done || scanState == ScanState.Failed) {
            kotlinx.coroutines.delay(3000)
            viewModel.resetScanState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posta in arrivo AI") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    when (scanState) {
                        ScanState.Running -> CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 4.dp),
                            strokeWidth = 2.dp
                        )
                        ScanState.Done -> Icon(
                            Icons.Filled.CheckCircle, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        ScanState.Failed -> Icon(
                            Icons.Filled.Error, contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        else -> IconButton(onClick = { viewModel.triggerScanNow() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Scansiona ora")
                        }
                    }
                    if (items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearProcessed() }) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = "Rimuovi elaborati")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            items.isEmpty() && scanState == ScanState.Idle ->
                EmptyInboxView(modifier = Modifier.padding(padding), onScanNow = { viewModel.triggerScanNow() })

            items.isEmpty() && scanState == ScanState.Running ->
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Analisi email in corso…", style = MaterialTheme.typography.bodyMedium)
                    }
                }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${items.size} ${if (items.size == 1) "elemento trovato" else "elementi trovati"} — approva o rifiuta:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(items, key = { it.id }) { item ->
                    var visible by remember { mutableStateOf(true) }
                    AnimatedVisibility(visible = visible, exit = shrinkVertically() + fadeOut()) {
                        PendingItemCard(
                            item = item,
                            onApprove = { date, time ->
                                visible = false
                                viewModel.approve(item, date, time)
                            },
                            onReject = {
                                visible = false
                                viewModel.reject(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PendingItemCard(
    item: PendingEmailItem,
    onApprove: (LocalDate, LocalTime?) -> Unit,
    onReject: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var editedDate by remember(item.id) { mutableStateOf(extractEffectiveDate(item)) }
    var editedTime by remember(item.id) { mutableStateOf(extractEffectiveTime(item)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var timeDialogValue by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (item.itemType) {
                PendingItemType.ACTIVITY -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                PendingItemType.REMINDER -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                PendingItemType.TODO     -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            // Title + type
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.itemType.emoji(), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        item.itemType.displayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Email source
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Email, contentDescription = null,
                    modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(
                    item.sourceEmailSubject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                "Da: ${item.sourceEmailFrom.substringBefore("<").trim()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // ── Data sempre visibile e modificabile ──────────────────────
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "📅 ${editedDate.format(italianDateFmt)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (item.itemType == PendingItemType.ACTIVITY) {
                            Text(
                                if (editedTime != null) "⏰ $editedTime" else "⏰ Nessun orario",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    // Modifica data
                    IconButton(onClick = { showDatePicker = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Modifica data",
                            modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    // Modifica orario (solo ACTIVITY)
                    if (item.itemType == PendingItemType.ACTIVITY) {
                        IconButton(
                            onClick = { timeDialogValue = editedTime?.toString() ?: ""; showTimePicker = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Filled.Schedule, contentDescription = "Modifica orario",
                                modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            // ─────────────────────────────────────────────────────────────

            if (item.aiReason.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "💡 ${item.aiReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expandable details
            if (item.payloadJson.isNotBlank()) {
                TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                    Text(if (expanded) "Nascondi dettagli" else "Mostra dettagli",
                        style = MaterialTheme.typography.labelSmall)
                    Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null, modifier = Modifier.size(16.dp))
                }
                if (expanded) {
                    Surface(shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(formatPayload(item), style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Rifiuta")
                }
                Button(
                    onClick = { onApprove(editedDate, editedTime) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Approva")
                }
            }

            Text(
                formatDate(item.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }

    // ── DatePickerDialog ─────────────────────────────────────────────────
    if (showDatePicker) {
        val initialMillis = editedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        editedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annulla") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── TimePickerDialog (solo ACTIVITY) ─────────────────────────────────
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Orario") },
            text = {
                OutlinedTextField(
                    value = timeDialogValue,
                    onValueChange = { timeDialogValue = it },
                    label = { Text("HH:mm") },
                    placeholder = { Text("es. 09:30") },
                    singleLine = true,
                    isError = timeDialogValue.isNotBlank() &&
                            runCatching { LocalTime.parse(timeDialogValue) }.isFailure
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = runCatching { LocalTime.parse(timeDialogValue) }.getOrNull()
                    if (timeDialogValue.isBlank() || parsed != null) {
                        editedTime = parsed
                        showTimePicker = false
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { editedTime = null; showTimePicker = false }) {
                        Text("Rimuovi", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { showTimePicker = false }) { Text("Annulla") }
                }
            }
        )
    }
}

@Composable
private fun EmptyInboxView(modifier: Modifier = Modifier, onScanNow: () -> Unit = {}) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.MarkEmailRead, contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))
            Text("Nessun elemento in attesa", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(
                "L'analisi automatica delle email gira ogni 6 ore.\nGli elementi trovati appariranno qui per la tua approvazione.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onScanNow) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Scansiona ora")
            }
        }
    }
}

// Restituisce la data effettiva che verrà usata (da payload o oggi come default)
private fun extractEffectiveDate(item: PendingEmailItem): LocalDate = try {
    val obj = com.google.gson.JsonParser.parseString(item.payloadJson).asJsonObject
    val key = if (item.itemType == PendingItemType.ACTIVITY) "scheduled_date" else "due_date"
    obj.get(key)?.takeIf { !it.isJsonNull }?.asString
        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: LocalDate.now()
} catch (e: Exception) { LocalDate.now() }

// Restituisce l'orario effettivo (solo per ACTIVITY)
private fun extractEffectiveTime(item: PendingEmailItem): LocalTime? = try {
    if (item.itemType != PendingItemType.ACTIVITY) null
    else {
        val obj = com.google.gson.JsonParser.parseString(item.payloadJson).asJsonObject
        obj.get("scheduled_time")?.takeIf { !it.isJsonNull }?.asString
            ?.takeIf { it.isNotBlank() && it != "null" }
            ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
    }
} catch (e: Exception) { null }

private fun formatDate(epochMs: Long): String = try {
    SimpleDateFormat("dd MMM HH:mm", Locale.ITALIAN).format(Date(epochMs))
} catch (e: Exception) { "" }

private fun formatPayload(item: PendingEmailItem): String = try {
    val obj = com.google.gson.JsonParser.parseString(item.payloadJson).asJsonObject
    obj.entrySet().joinToString("\n") { (k, v) ->
        val label = when (k) {
            "title" -> "Titolo"; "type" -> "Tipo"
            "scheduled_date" -> "Data"; "scheduled_time" -> "Orario"
            "duration_minutes" -> "Durata (min)"; "description" -> "Descrizione"
            "calories_burned" -> "Calorie"; "category" -> "Categoria"
            "due_date" -> "Scadenza"; "amount" -> "Importo (€)"
            "is_important" -> "Importante"; else -> k
        }
        "$label: ${if (v.isJsonNull) "—" else v.asString}"
    }
} catch (e: Exception) { item.payloadJson }

package com.appfit.ui.workouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.Activity
import com.appfit.data.model.ActivityType
import com.appfit.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    onOpenDrawer: () -> Unit = {},
    onActivityClick: (Long) -> Unit = {},
    viewModel: WorkoutsViewModel = hiltViewModel()
) {
    val weekStart by viewModel.weekStart.collectAsStateWithLifecycle()
    val activities by viewModel.activities.collectAsStateWithLifecycle()

    val weekEnd = weekStart.plusDays(6)
    val weekFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN)

    // Group activities by date
    val grouped = activities.groupBy { it.scheduledDate }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Allenamenti", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Week navigator
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = viewModel::prevWeek) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Settimana precedente",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text(
                        text = "${weekStart.format(weekFormatter)} – ${weekEnd.format(weekFormatter)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    IconButton(onClick = viewModel::nextWeek) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Settimana successiva",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            if (grouped.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Nessun allenamento questa settimana",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Chiedilo all'AI Chat!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Iterate all 7 days of the week, show only those with activities
                    val dayFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)
                    val daysWithActivities = (0..6)
                        .map { weekStart.plusDays(it.toLong()) }
                        .filter { grouped.containsKey(it) }

                    daysWithActivities.forEach { date ->
                        val dayActivities = grouped[date] ?: emptyList()
                        item(key = "header_$date") {
                            DayHeader(date = date, count = dayActivities.size, formatter = dayFormatter)
                        }
                        items(dayActivities, key = { it.id }) { activity ->
                            ExpandableWorkoutCard(
                                activity = activity,
                                onDetailClick = { onActivityClick(activity.id) },
                                onToggleComplete = {
                                    viewModel.toggleCompleted(activity.id, activity.isCompleted)
                                },
                                onSave = { updated -> viewModel.updateActivity(updated) },
                                onDelete = { viewModel.deleteActivity(activity.id) }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate, count: Int, formatter: DateTimeFormatter) {
    val isToday = date == LocalDate.now()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Text(
            text = date.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (isToday) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    "oggi",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                "$count",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableWorkoutCard(
    activity: Activity,
    onDetailClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onSave: (Activity) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember(activity.id) { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Edit state — initialized from activity
    var editTitle by remember(activity.id) { mutableStateOf(activity.title) }
    var editType by remember(activity.id) { mutableStateOf(activity.type) }
    var editDuration by remember(activity.id) { mutableStateOf(activity.durationMinutes.toString()) }
    var editTime by remember(activity.id) { mutableStateOf(activity.scheduledTime?.toString() ?: "") }
    var editCalories by remember(activity.id) { mutableStateOf(if (activity.caloriesBurned > 0) activity.caloriesBurned.toString() else "") }
    var editDescription by remember(activity.id) { mutableStateOf(activity.description) }
    var typeMenuExpanded by remember { mutableStateOf(false) }

    val activityColor = when (activity.type) {
        ActivityType.CARDIO -> CardioColor
        ActivityType.STRENGTH -> StrengthColor
        ActivityType.FLEXIBILITY -> FlexibilityColor
        ActivityType.YOGA -> YogaColor
        ActivityType.REST -> RestColor
        ActivityType.CUSTOM -> CustomColor
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina allenamento") },
            text = { Text("Vuoi eliminare \"${activity.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Elimina") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annulla") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Collapsed header row — always visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 48.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(activityColor)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (activity.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (activity.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(activity.type.displayName(), fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = activityColor.copy(alpha = 0.15f)
                            )
                        )
                        Icon(Icons.Filled.Timer, contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${activity.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (activity.scheduledTime != null) {
                            Icon(Icons.Filled.Schedule, contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                activity.scheduledTime.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Checkbox(
                    checked = activity.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Comprimi" else "Espandi",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded edit section
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Title
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Titolo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Type dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeMenuExpanded,
                        onExpandedChange = { typeMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = editType.displayName(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = typeMenuExpanded,
                            onDismissRequest = { typeMenuExpanded = false }
                        ) {
                            ActivityType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.displayName()) },
                                    onClick = { editType = type; typeMenuExpanded = false }
                                )
                            }
                        }
                    }

                    // Duration + Time
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editDuration,
                            onValueChange = { editDuration = it },
                            label = { Text("Durata (min)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editTime,
                            onValueChange = { editTime = it },
                            label = { Text("Ora (HH:mm)") },
                            placeholder = { Text("es. 07:30") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Calories
                    OutlinedTextField(
                        value = editCalories,
                        onValueChange = { editCalories = it },
                        label = { Text("Calorie bruciate") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Description
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Descrizione") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5
                    )

                    // Action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Elimina")
                        }
                        Button(
                            onClick = {
                                val parsedTime = editTime.takeIf { it.isNotBlank() }?.let {
                                    try { LocalTime.parse(it) } catch (e: Exception) { null }
                                }
                                onSave(
                                    activity.copy(
                                        title = editTitle.trim().ifBlank { activity.title },
                                        type = editType,
                                        durationMinutes = editDuration.toIntOrNull() ?: activity.durationMinutes,
                                        scheduledTime = parsedTime,
                                        caloriesBurned = editCalories.toIntOrNull() ?: 0,
                                        description = editDescription.trim()
                                    )
                                )
                                expanded = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Salva")
                        }
                    }
                }
            }
        }
    }
}

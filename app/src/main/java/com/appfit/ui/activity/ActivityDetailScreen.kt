package com.appfit.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.ActivityType
import com.appfit.ui.common.UiState
import com.appfit.ui.theme.*
import com.appfit.ui.theme.GradientTopAppBar
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    onBack: () -> Unit,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina allenamento") },
            text = { Text("Vuoi eliminare questa attività? L'operazione non è reversibile.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteActivity { onBack() }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Elimina") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annulla") }
            }
        )
    }

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = { Text(if (isEditMode) "Modifica attività" else "Dettaglio attività") },
                navigationIcon = {
                    IconButton(onClick = { if (isEditMode) isEditMode = false else onBack() }) {
                        Icon(
                            if (isEditMode) Icons.Filled.Close else Icons.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Modifica")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Elimina",
                                tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                val activity = state.data
                val activityColor = when (activity.type) {
                    ActivityType.CARDIO -> CardioColor
                    ActivityType.STRENGTH -> StrengthColor
                    ActivityType.FLEXIBILITY -> FlexibilityColor
                    ActivityType.YOGA -> YogaColor
                    ActivityType.REST -> RestColor
                    ActivityType.CUSTOM -> CustomColor
                }

                if (isEditMode) {
                    // Edit mode
                    var editTitle by remember(activity.id) { mutableStateOf(activity.title) }
                    var editType by remember(activity.id) { mutableStateOf(activity.type) }
                    var editDuration by remember(activity.id) { mutableStateOf(activity.durationMinutes.toString()) }
                    var editTime by remember(activity.id) { mutableStateOf(activity.scheduledTime?.toString() ?: "") }
                    var editCalories by remember(activity.id) { mutableStateOf(if (activity.caloriesBurned > 0) activity.caloriesBurned.toString() else "") }
                    var editDescription by remember(activity.id) { mutableStateOf(activity.description) }
                    var typeMenuExpanded by remember { mutableStateOf(false) }
                    var titleError by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it; titleError = false },
                            label = { Text("Titolo *") },
                            isError = titleError,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

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

                        OutlinedTextField(
                            value = editCalories,
                            onValueChange = { editCalories = it },
                            label = { Text("Calorie bruciate") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Descrizione") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6
                        )

                        Button(
                            onClick = {
                                if (editTitle.isBlank()) { titleError = true; return@Button }
                                val parsedTime = editTime.takeIf { it.isNotBlank() }?.let {
                                    try { LocalTime.parse(it) } catch (e: Exception) { null }
                                }
                                viewModel.updateActivity(
                                    title = editTitle.trim(),
                                    type = editType,
                                    durationMinutes = editDuration.toIntOrNull() ?: activity.durationMinutes,
                                    scheduledTime = parsedTime,
                                    caloriesBurned = editCalories.toIntOrNull() ?: 0,
                                    description = editDescription.trim()
                                )
                                isEditMode = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Salva modifiche")
                        }
                    }
                } else {
                    // View mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header con colore tipo
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = activityColor.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(activityColor.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (activity.type) {
                                            ActivityType.CARDIO -> Icons.Filled.DirectionsRun
                                            ActivityType.STRENGTH -> Icons.Filled.FitnessCenter
                                            ActivityType.FLEXIBILITY, ActivityType.YOGA -> Icons.Filled.SelfImprovement
                                            ActivityType.REST -> Icons.Filled.Bedtime
                                            ActivityType.CUSTOM -> Icons.Filled.Star
                                        },
                                        contentDescription = null,
                                        tint = activityColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = activity.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = activity.type.displayName(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = activityColor
                                    )
                                }
                            }
                        }

                        // Info chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoChip(
                                icon = Icons.Filled.Timer,
                                label = "${activity.durationMinutes} min",
                                modifier = Modifier.weight(1f)
                            )
                            if (activity.caloriesBurned > 0) {
                                InfoChip(
                                    icon = Icons.Filled.LocalFireDepartment,
                                    label = "${activity.caloriesBurned} kcal",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (activity.scheduledTime != null) {
                                InfoChip(
                                    icon = Icons.Filled.Schedule,
                                    label = activity.scheduledTime.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Descrizione
                        if (activity.description.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Descrizione",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = activity.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        if (activity.aiGenerated) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Generata dall'AI") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.SmartToy,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }

                        // Toggle completata
                        Button(
                            onClick = { viewModel.toggleCompleted() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = if (activity.isCompleted) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            } else {
                                ButtonDefaults.buttonColors()
                            }
                        ) {
                            Icon(
                                imageVector = if (activity.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.CheckCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (activity.isCompleted) "Completata" else "Segna come completata")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

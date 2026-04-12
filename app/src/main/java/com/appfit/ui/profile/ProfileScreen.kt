package com.appfit.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.ai.AiProvider
import com.appfit.ai.ApiKeyProvider

private val WORKOUT_TYPES = listOf(
    "CARDIO" to "Cardio",
    "STRENGTH" to "Forza",
    "FLEXIBILITY" to "Flessibilità",
    "YOGA" to "Yoga",
    "REST" to "Riposo attivo",
    "CUSTOM" to "Personalizzato"
)

private val FITNESS_GOALS = listOf(
    "weight_loss" to "Perdita di peso",
    "muscle_gain" to "Aumento massa muscolare",
    "endurance" to "Miglioramento resistenza",
    "flexibility" to "Miglioramento flessibilità",
    "general_health" to "Salute generale"
)

private val DIETARY_OPTIONS = listOf(
    "vegetariano", "vegano", "senza glutine", "senza lattosio",
    "halal", "kosher", "senza uova", "senza frutta a guscio"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val currentProvider by viewModel.currentProvider.collectAsStateWithLifecycle()
    val currentModel by viewModel.currentModel.collectAsStateWithLifecycle()
    val aiSaveResult by viewModel.aiSaveResult.collectAsStateWithLifecycle()

    // Dati fisici
    var weightText by remember(profile.weightKg) { mutableStateOf(profile.weightKg?.toString() ?: "") }
    var heightText by remember(profile.heightCm) { mutableStateOf(profile.heightCm?.toString() ?: "") }
    var ageText by remember(profile.age) { mutableStateOf(profile.age?.toString() ?: "") }
    var monthlyUpdate by remember(profile.monthlyUpdateEnabled) { mutableStateOf(profile.monthlyUpdateEnabled) }

    // AI settings
    var selectedAiProvider by remember(currentProvider) { mutableStateOf(currentProvider) }
    var selectedAiModel by remember(currentModel) { mutableStateOf(currentModel) }
    var aiApiKey by remember { mutableStateOf("") }
    var showAiKey by remember { mutableStateOf(false) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedAiProvider) {
        // Reset model when provider changes
        selectedAiModel = when (selectedAiProvider) {
            AiProvider.ANTHROPIC -> ApiKeyProvider.CLAUDE_MODELS.first()
            AiProvider.GEMINI -> ApiKeyProvider.GEMINI_MODELS.first()
        }
        aiApiKey = ""
    }

    var aiSaved by remember { mutableStateOf(false) }
    LaunchedEffect(aiSaveResult) {
        if (aiSaveResult is ProfileViewModel.AiSaveResult.Saved) {
            aiSaved = true
            viewModel.resetAiSaveResult()
        }
    }

    // Preferenze
    var selectedWorkoutTypes by remember(profile.preferredWorkoutTypes) {
        mutableStateOf(profile.preferredWorkoutTypes.toSet())
    }
    var selectedGoal by remember(profile.fitnessGoal) { mutableStateOf(profile.fitnessGoal) }
    var selectedDietary by remember(profile.dietaryRestrictions) {
        mutableStateOf(profile.dietaryRestrictions.toSet())
    }

    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilo e preferenze") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    if (saved) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Sezione: Dati fisici ---
            SectionTitle("Dati fisici", Icons.Filled.Person)

            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it; saved = false },
                label = { Text("Peso") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = { Text("kg", modifier = Modifier.padding(end = 12.dp)) },
                singleLine = true
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it; saved = false },
                label = { Text("Altezza") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text("cm", modifier = Modifier.padding(end = 12.dp)) },
                singleLine = true
            )
            OutlinedTextField(
                value = ageText,
                onValueChange = { ageText = it; saved = false },
                label = { Text("Età") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text("anni", modifier = Modifier.padding(end = 12.dp)) },
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Promemoria mensile", fontWeight = FontWeight.Medium)
                    Text(
                        "Notifica ogni mese per aggiornare il peso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = monthlyUpdate, onCheckedChange = { monthlyUpdate = it; saved = false })
            }

            HorizontalDivider()

            // --- Sezione: Obiettivo fitness ---
            SectionTitle("Obiettivo fitness", Icons.Filled.TrackChanges)
            Text(
                "L'AI userà questo obiettivo per personalizzare allenamenti e dieta.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                FITNESS_GOALS.forEach { (key, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGoal == key,
                            onClick = { selectedGoal = if (selectedGoal == key) null else key; saved = false }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }

            HorizontalDivider()

            // --- Sezione: Allenamenti preferiti ---
            SectionTitle("Allenamenti preferiti", Icons.Filled.FitnessCenter)
            Text(
                "L'AI darà priorità a questi tipi di attività.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                WORKOUT_TYPES.forEach { (key, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = key in selectedWorkoutTypes,
                            onCheckedChange = { checked ->
                                selectedWorkoutTypes = if (checked)
                                    selectedWorkoutTypes + key
                                else
                                    selectedWorkoutTypes - key
                                saved = false
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }

            HorizontalDivider()

            // --- Sezione: Preferenze alimentari ---
            SectionTitle("Preferenze alimentari", Icons.Filled.Restaurant)
            Text(
                "L'AI rispetterà sempre queste restrizioni quando suggerisce pasti.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DIETARY_OPTIONS.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = option in selectedDietary,
                            onCheckedChange = { checked ->
                                selectedDietary = if (checked)
                                    selectedDietary + option
                                else
                                    selectedDietary - option
                                saved = false
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(option.replaceFirstChar { it.uppercase() })
                    }
                }
            }

            HorizontalDivider()

            // --- Sezione: Impostazioni AI ---
            SectionTitle("Assistente AI", Icons.Filled.SmartToy)
            Text(
                "Scegli il modello AI e aggiorna la chiave API.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Provider toggle
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedAiProvider == AiProvider.ANTHROPIC,
                    onClick = { selectedAiProvider = AiProvider.ANTHROPIC },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("Claude") }
                SegmentedButton(
                    selected = selectedAiProvider == AiProvider.GEMINI,
                    onClick = { selectedAiProvider = AiProvider.GEMINI },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("Gemini") }
            }

            // Model dropdown
            val modelsForProvider = when (selectedAiProvider) {
                AiProvider.ANTHROPIC -> ApiKeyProvider.CLAUDE_MODELS
                AiProvider.GEMINI -> ApiKeyProvider.GEMINI_MODELS
            }
            ExposedDropdownMenuBox(
                expanded = modelDropdownExpanded,
                onExpandedChange = { modelDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedAiModel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Modello") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = modelDropdownExpanded,
                    onDismissRequest = { modelDropdownExpanded = false }
                ) {
                    modelsForProvider.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model, style = MaterialTheme.typography.bodyMedium) },
                            onClick = { selectedAiModel = model; modelDropdownExpanded = false }
                        )
                    }
                }
            }

            // API key field
            OutlinedTextField(
                value = aiApiKey,
                onValueChange = { aiApiKey = it; aiSaved = false },
                label = {
                    Text(when (selectedAiProvider) {
                        AiProvider.ANTHROPIC -> "API Key Anthropic (opzionale)"
                        AiProvider.GEMINI -> "API Key Gemini (opzionale)"
                    })
                },
                placeholder = {
                    Text(when (selectedAiProvider) {
                        AiProvider.ANTHROPIC -> "sk-ant-… (lascia vuoto per non cambiare)"
                        AiProvider.GEMINI -> "AIza… (lascia vuoto per non cambiare)"
                    })
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showAiKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showAiKey = !showAiKey }) {
                        Icon(
                            if (showAiKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                },
                isError = aiSaveResult is ProfileViewModel.AiSaveResult.Error,
                supportingText = {
                    when (val r = aiSaveResult) {
                        is ProfileViewModel.AiSaveResult.Error -> Text(r.message, color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }
                },
                singleLine = true
            )

            Button(
                onClick = { viewModel.saveAiSettings(selectedAiProvider, aiApiKey, selectedAiModel) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (aiSaved) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Impostazioni AI salvate")
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Salva impostazioni AI")
                }
            }

            HorizontalDivider()

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.saveProfile(
                        weightKg = weightText.toFloatOrNull(),
                        heightCm = heightText.toIntOrNull(),
                        age = ageText.toIntOrNull(),
                        monthlyUpdateEnabled = monthlyUpdate
                    )
                    viewModel.savePreferences(
                        workoutTypes = selectedWorkoutTypes.toList().ifEmpty { null },
                        dietaryRestrictions = selectedDietary.toList().ifEmpty { null },
                        fitnessGoal = selectedGoal
                    )
                    saved = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salva profilo", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

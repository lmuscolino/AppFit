package com.appfit.ui.onboarding

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.ai.AiProvider

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val saveResult by viewModel.saveResult.collectAsStateWithLifecycle()
    var step by remember { mutableStateOf(0) }

    LaunchedEffect(saveResult) {
        when (saveResult) {
            is OnboardingViewModel.SaveResult.ApiKeySaved -> {
                step = 1
                viewModel.resetSaveResult()
            }
            is OnboardingViewModel.SaveResult.Success -> onComplete()
            else -> {}
        }
    }

    when (step) {
        0 -> ApiKeyStep(
            viewModel = viewModel,
            saveResult = saveResult,
            onSave = { key -> viewModel.saveApiKey(key) }
        )
        1 -> ProfileStep(
            onBack = { step = 0 },
            onSave = { weight, height, age, monthly ->
                viewModel.saveProfile(weight, height, age, monthly)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApiKeyStep(
    viewModel: OnboardingViewModel,
    saveResult: OnboardingViewModel.SaveResult,
    onSave: (String) -> Unit
) {
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()

    var apiKey by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    // Reset key field when provider changes
    LaunchedEffect(selectedProvider) { apiKey = "" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AppFit",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Il tuo assistente personale per fitness e dieta con AI",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        FeatureRow(Icons.Filled.CalendarMonth, "Calendario attività e dieta")
        FeatureRow(Icons.Filled.SmartToy, "AI che modifica il tuo piano")
        FeatureRow(Icons.Filled.ShoppingCart, "Lista della spesa automatica")
        FeatureRow(Icons.Filled.Notifications, "Notifiche per le attività")

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.SmartToy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Scegli il tuo AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Provider toggle
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedProvider == AiProvider.ANTHROPIC,
                        onClick = { viewModel.setProvider(AiProvider.ANTHROPIC) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Claude")
                    }
                    SegmentedButton(
                        selected = selectedProvider == AiProvider.GEMINI,
                        onClick = { viewModel.setProvider(AiProvider.GEMINI) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Gemini")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Model dropdown
                ExposedDropdownMenuBox(
                    expanded = modelDropdownExpanded,
                    onExpandedChange = { modelDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedModel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Modello") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = modelDropdownExpanded,
                        onDismissRequest = { modelDropdownExpanded = false }
                    ) {
                        viewModel.modelsForCurrentProvider().forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    viewModel.setModel(model)
                                    modelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // API key field
                val keyLabel = when (selectedProvider) {
                    AiProvider.ANTHROPIC -> "API Key Anthropic"
                    AiProvider.GEMINI -> "API Key Google Gemini"
                }
                val keyPlaceholder = when (selectedProvider) {
                    AiProvider.ANTHROPIC -> "sk-ant-…"
                    AiProvider.GEMINI -> "AIza…"
                }

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text(keyLabel) },
                    placeholder = { Text(keyPlaceholder) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showKey) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showKey) "Nascondi" else "Mostra"
                            )
                        }
                    },
                    isError = saveResult is OnboardingViewModel.SaveResult.Error,
                    supportingText = {
                        if (saveResult is OnboardingViewModel.SaveResult.Error) {
                            Text(
                                (saveResult as OnboardingViewModel.SaveResult.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                val consoleLabel = when (selectedProvider) {
                    AiProvider.ANTHROPIC -> "console.anthropic.com"
                    AiProvider.GEMINI -> "aistudio.google.com"
                }
                TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Ottieni la tua API Key su $consoleLabel",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSave(apiKey) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = apiKey.isNotBlank()
        ) {
            Text("Avanti", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun ProfileStep(
    onBack: () -> Unit,
    onSave: (Float?, Int?, Int?, Boolean) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var monthlyUpdate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Il tuo profilo fisico",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Questi dati aiutano l'AI a creare piani più personalizzati. Tutti i campi sono opzionali.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Peso (kg)") },
                    placeholder = { Text("es. 75") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    trailingIcon = { Text("kg", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 12.dp)) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("Altezza (cm)") },
                    placeholder = { Text("es. 175") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Text("cm", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 12.dp)) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text("Età") },
                    placeholder = { Text("es. 30") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Text("anni", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 12.dp)) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Promemoria mensile",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Ricevi una notifica ogni mese per aggiornare peso e statistiche",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = monthlyUpdate,
                        onCheckedChange = { monthlyUpdate = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Indietro")
            }
            Button(
                onClick = {
                    val weight = weightText.toFloatOrNull()
                    val height = heightText.toIntOrNull()
                    val age = ageText.toIntOrNull()
                    onSave(weight, height, age, monthlyUpdate)
                },
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Icon(Icons.Filled.RocketLaunch, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inizia")
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

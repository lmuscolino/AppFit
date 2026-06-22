package com.appfit.ui.profile

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.ai.AiProvider
import com.appfit.ai.ApiKeyProvider
import com.appfit.ai.GoogleCalendarProvider
import com.appfit.ai.GoogleMailProvider
import com.appfit.ai.UserNote
import com.appfit.ai.WorkoutScheduleSlot
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import java.time.DayOfWeek

private val FITNESS_LEVELS = listOf(
    "beginner"     to "Principiante – mi alleno raramente o sono agli inizi",
    "intermediate" to "Intermedio – mi alleno regolarmente da qualche mese",
    "advanced"     to "Avanzato – mi alleno intensamente da anni"
)

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
    val googleAccount by viewModel.googleAccount.collectAsStateWithLifecycle()
    val gcalSyncEnabled by viewModel.googleCalendarSyncEnabled.collectAsStateWithLifecycle()
    val gmailEnabled by viewModel.gmailScanEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var googleSignInError by remember { mutableStateOf<String?>(null) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val email = account?.email
            if (email != null) {
                viewModel.connectGoogle(email)
            } else {
                googleSignInError = "Impossibile ottenere l'email dell'account"
            }
        } catch (e: ApiException) {
            if (e.statusCode != 12501) { // 12501 = cancelled by user
                googleSignInError = "Errore accesso Google (${e.statusCode})"
            }
        }
    }

    val currentProvider by viewModel.currentProvider.collectAsStateWithLifecycle()
    val currentModel by viewModel.currentModel.collectAsStateWithLifecycle()
    val aiSaveResult by viewModel.aiSaveResult.collectAsStateWithLifecycle()
    val storedAnthropicKey by viewModel.anthropicKey.collectAsStateWithLifecycle()
    val storedGeminiKey by viewModel.geminiKey.collectAsStateWithLifecycle()

    // Dati fisici
    var selectedSex by remember(profile.sex) { mutableStateOf(profile.sex) }
    var weightText by remember(profile.weightKg) { mutableStateOf(profile.weightKg?.toString() ?: "") }
    var heightText by remember(profile.heightCm) { mutableStateOf(profile.heightCm?.toString() ?: "") }
    var ageText by remember(profile.age) { mutableStateOf(profile.age?.toString() ?: "") }
    var monthlyUpdate by remember(profile.monthlyUpdateEnabled) { mutableStateOf(profile.monthlyUpdateEnabled) }

    // AI settings – provider e chiavi separate per provider
    var selectedAiProvider by remember(currentProvider) { mutableStateOf(currentProvider) }
    // Ogni provider mantiene la propria chiave e il proprio modello
    var claudeKeyText by remember(storedAnthropicKey) { mutableStateOf(storedAnthropicKey) }
    var geminiKeyText by remember(storedGeminiKey) { mutableStateOf(storedGeminiKey) }
    var claudeModel by remember(currentProvider, currentModel) {
        mutableStateOf(if (currentProvider == AiProvider.ANTHROPIC) currentModel else ApiKeyProvider.CLAUDE_MODELS.first())
    }
    var geminiModel by remember(currentProvider, currentModel) {
        mutableStateOf(if (currentProvider == AiProvider.GEMINI) currentModel else ApiKeyProvider.GEMINI_MODELS.first())
    }
    val aiApiKey = when (selectedAiProvider) {
        AiProvider.ANTHROPIC -> claudeKeyText
        AiProvider.GEMINI -> geminiKeyText
    }
    val selectedAiModel = when (selectedAiProvider) {
        AiProvider.ANTHROPIC -> claudeModel
        AiProvider.GEMINI -> geminiModel
    }
    var showAiKey by remember { mutableStateOf(false) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

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
    var selectedFitnessLevel by remember(profile.fitnessLevel) { mutableStateOf(profile.fitnessLevel) }
    var selectedDietary by remember(profile.dietaryRestrictions) {
        mutableStateOf(profile.dietaryRestrictions.toSet())
    }

    var saved by remember { mutableStateOf(false) }

    // Note personali
    val notes = profile.userNotes
    var showNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<UserNote?>(null) }
    var noteText by remember { mutableStateOf("") }

    // Fasce orarie allenamento
    var scheduleSlots by remember(profile.workoutSchedule) {
        mutableStateOf(profile.workoutSchedule.toMutableList())
    }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var editingSlotIndex by remember { mutableStateOf<Int?>(null) }

    // Dialog aggiungi/modifica nota
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false; editingNote = null; noteText = "" },
            title = { Text(if (editingNote != null) "Modifica nota" else "Aggiungi nota") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Nota") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 5
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            val en = editingNote
                            if (en != null) viewModel.updateNote(en.id, noteText)
                            else viewModel.addNote(noteText)
                        }
                        showNoteDialog = false; editingNote = null; noteText = ""
                    }
                ) { Text("Salva") }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false; editingNote = null; noteText = "" }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Dialog aggiungi/modifica fascia oraria
    if (showScheduleDialog) {
        WorkoutScheduleDialog(
            initial = editingSlotIndex?.let { scheduleSlots.getOrNull(it) },
            onDismiss = { showScheduleDialog = false; editingSlotIndex = null },
            onSave = { slot ->
                val idx = editingSlotIndex
                scheduleSlots = scheduleSlots.toMutableList().also {
                    if (idx != null) it[idx] = slot else it.add(slot)
                }
                viewModel.saveWorkoutSchedule(scheduleSlots)
                showScheduleDialog = false
                editingSlotIndex = null
                saved = false
            }
        )
    }

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

            Text(
                "Sesso biologico",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedSex == "male",
                    onClick = { selectedSex = if (selectedSex == "male") null else "male"; saved = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("Maschio") }
                SegmentedButton(
                    selected = selectedSex == "female",
                    onClick = { selectedSex = if (selectedSex == "female") null else "female"; saved = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("Femmina") }
            }

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

            // --- Sezione: Livello di forma fisica ---
            SectionTitle("Livello di forma fisica", Icons.Filled.DirectionsRun)
            Text(
                "L'AI adatterà l'intensità degli allenamenti al tuo livello attuale.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                FITNESS_LEVELS.forEach { (key, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFitnessLevel == key,
                            onClick = { selectedFitnessLevel = if (selectedFitnessLevel == key) null else key; saved = false }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
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

            // --- Sezione: Fasce orarie allenamento ---
            SectionTitle("Fasce orarie di allenamento", Icons.Filled.Schedule)
            Text(
                "L'AI pianificherà gli allenamenti in questi giorni e orari.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (scheduleSlots.isEmpty()) {
                Text(
                    "Nessuna fascia oraria impostata. Aggiungine una o scrivi nella chat (es. \"mi alleno lun/mer/ven dalle 7 alle 9\").",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                scheduleSlots.forEachIndexed { index, slot ->
                    WorkoutScheduleSlotCard(
                        slot = slot,
                        onEdit = { editingSlotIndex = index; showScheduleDialog = true },
                        onDelete = {
                            scheduleSlots = scheduleSlots.toMutableList().also { it.removeAt(index) }
                            viewModel.saveWorkoutSchedule(scheduleSlots)
                        }
                    )
                }
            }
            OutlinedButton(
                onClick = { editingSlotIndex = null; showScheduleDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Aggiungi fascia oraria")
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

            // --- Sezione: Note personali ---
            SectionTitle("Note personali", Icons.Filled.Notes)
            Text(
                "Informazioni che l'AI usa per personalizzare ogni risposta. L'AI può aggiornare queste note durante la chat.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (notes.isEmpty()) {
                Text(
                    "Nessuna nota salvata. Aggiungine una o scrivi nella chat (es. \"ho un'ernia al disco\").",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                notes.forEach { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = note.content,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = {
                                editingNote = note
                                noteText = note.content
                                showNoteDialog = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Modifica", modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { viewModel.deleteNote(note.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { noteText = ""; editingNote = null; showNoteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Aggiungi nota")
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
                            onClick = {
                                when (selectedAiProvider) {
                                    AiProvider.ANTHROPIC -> claudeModel = model
                                    AiProvider.GEMINI -> geminiModel = model
                                }
                                modelDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // API key field
            OutlinedTextField(
                value = aiApiKey,
                onValueChange = { v ->
                    when (selectedAiProvider) {
                        AiProvider.ANTHROPIC -> { claudeKeyText = v; aiSaved = false }
                        AiProvider.GEMINI -> { geminiKeyText = v; aiSaved = false }
                    }
                },
                label = {
                    Text(when (selectedAiProvider) {
                        AiProvider.ANTHROPIC -> "API Key Anthropic"
                        AiProvider.GEMINI -> "API Key Gemini"
                    })
                },
                placeholder = {
                    Text(when (selectedAiProvider) {
                        AiProvider.ANTHROPIC -> "sk-ant-…"
                        AiProvider.GEMINI -> "AIza…"
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

            // --- Sezione: Account Google ---
            SectionTitle("Account Google", Icons.Filled.AccountCircle)

            if (googleAccount != null) {
                // Account collegato
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Account collegato",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                googleAccount!!,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        TextButton(onClick = { viewModel.disconnectGoogle() }) {
                            Text("Disconnetti", color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Switch Google Calendar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Sync Google Calendar", style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium)
                            Text("Le attività AppFit appaiono in Google Calendar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = gcalSyncEnabled,
                        onCheckedChange = { viewModel.setCalendarSyncEnabled(it) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Switch Gmail scan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Email, contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Scan Gmail automatico", style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium)
                            Text("Rileva bollette, scadenze e abbonamenti ogni 6 ore",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = gmailEnabled,
                        onCheckedChange = { viewModel.setGmailScanEnabled(it) }
                    )
                }

            } else {
                Text(
                    "Collega il tuo account Google per sincronizzare le attività con Google Calendar e rilevare automaticamente bollette e scadenze dalle email.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                if (googleSignInError != null) {
                    Text(
                        googleSignInError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Button(
                    onClick = {
                        googleSignInError = null
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .requestScopes(
                                Scope(GoogleCalendarProvider.CALENDAR_SCOPE.removePrefix("oauth2:")),
                                Scope(GoogleMailProvider.GMAIL_SCOPE.removePrefix("oauth2:"))
                            )
                            .build()
                        val client = GoogleSignIn.getClient(context, gso)
                        googleSignInLauncher.launch(client.signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Collega account Google")
                }
            }

            HorizontalDivider()

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.saveProfile(
                        sex = selectedSex,
                        weightKg = weightText.toFloatOrNull(),
                        heightCm = heightText.toIntOrNull(),
                        age = ageText.toIntOrNull(),
                        monthlyUpdateEnabled = monthlyUpdate
                    )
                    viewModel.savePreferences(
                        workoutTypes = selectedWorkoutTypes.toList().ifEmpty { null },
                        dietaryRestrictions = selectedDietary.toList().ifEmpty { null },
                        fitnessGoal = selectedGoal,
                        fitnessLevel = selectedFitnessLevel
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

private val DAYS_OF_WEEK = listOf(
    DayOfWeek.MONDAY    to "Lun",
    DayOfWeek.TUESDAY   to "Mar",
    DayOfWeek.WEDNESDAY to "Mer",
    DayOfWeek.THURSDAY  to "Gio",
    DayOfWeek.FRIDAY    to "Ven",
    DayOfWeek.SATURDAY  to "Sab",
    DayOfWeek.SUNDAY    to "Dom"
)

@Composable
private fun WorkoutScheduleSlotCard(
    slot: WorkoutScheduleSlot,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dayLabels = DAYS_OF_WEEK
        .filter { (dow, _) -> dow.name in slot.days }
        .joinToString(", ") { it.second }
    val timeLabel = when {
        slot.startTime.isNotBlank() && slot.endTime.isNotBlank() -> "${slot.startTime} – ${slot.endTime}"
        slot.startTime.isNotBlank() -> "dalle ${slot.startTime}"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dayLabels.ifBlank { "Nessun giorno" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (timeLabel.isNotBlank()) {
                    Text(
                        text = timeLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Modifica", modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Elimina",
                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutScheduleDialog(
    initial: WorkoutScheduleSlot?,
    onDismiss: () -> Unit,
    onSave: (WorkoutScheduleSlot) -> Unit
) {
    var selectedDays by remember(initial) {
        mutableStateOf(initial?.days?.mapNotNull {
            runCatching { DayOfWeek.valueOf(it) }.getOrNull()
        }?.toSet() ?: emptySet())
    }
    var startTime by remember(initial) { mutableStateOf(initial?.startTime ?: "") }
    var endTime by remember(initial) { mutableStateOf(initial?.endTime ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial != null) "Modifica fascia oraria" else "Aggiungi fascia oraria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Giorni", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                // Day chips grid: 4 + 3
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DAYS_OF_WEEK.take(4).forEach { (dow, label) ->
                            val selected = dow in selectedDays
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedDays = if (selected) selectedDays - dow else selectedDays + dow
                                },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DAYS_OF_WEEK.drop(4).forEach { (dow, label) ->
                            val selected = dow in selectedDays
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedDays = if (selected) selectedDays - dow else selectedDays + dow
                                },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Placeholder to align with 4-column row above
                        Spacer(Modifier.weight(1f))
                    }
                }
                Text("Fascia oraria (opzionale)", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Dalle") },
                        placeholder = { Text("07:00") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Alle") },
                        placeholder = { Text("09:00") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(WorkoutScheduleSlot(
                        days = selectedDays.map { it.name }.toSet(),
                        startTime = startTime.trim(),
                        endTime = endTime.trim()
                    ))
                },
                enabled = selectedDays.isNotEmpty()
            ) { Text("Salva") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
private fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

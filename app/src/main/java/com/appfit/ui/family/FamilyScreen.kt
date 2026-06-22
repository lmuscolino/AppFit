package com.appfit.ui.family

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.appfit.data.model.ActivityType
import com.appfit.data.model.FamilyActivityProposal
import com.appfit.data.model.FamilyMember
import com.appfit.data.model.FamilyTodo
import com.appfit.data.model.SharedExpense
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import com.appfit.ui.theme.GradientTopAppBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScreen(
    onOpenDrawer: () -> Unit = {},
    viewModel: FamilyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("824194992540-6l0rouf4fm880n85qqtgv1okotrvgv47.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Tenta il silent sign-in automatico appena si entra nella sezione Famiglia.
    // Se l'utente è già connesso con Google sul dispositivo, Firebase viene autenticato
    // senza mostrare nessuna schermata di accesso.
    LaunchedEffect(uiState) {
        if (uiState is FamilyUiState.NotSignedIn) {
            runCatching {
                val account = googleSignInClient.silentSignIn().await()
                account?.idToken?.let { viewModel.signInWithGoogle(it) }
            }
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("FamilySignIn", "resultCode=${result.resultCode} data=${result.data}")
        if (result.data == null) {
            viewModel.setError("Sign-In annullato (nessun dato, resultCode=${result.resultCode})")
            return@rememberLauncherForActivityResult
        }
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        runCatching { task.getResult(ApiException::class.java) }
            .onSuccess { account ->
                if (account?.idToken != null) {
                    viewModel.signInWithGoogle(account.idToken!!)
                } else {
                    viewModel.setError("Nessun token (account=${account?.email})")
                }
            }
            .onFailure { e ->
                val code = (e as? ApiException)?.statusCode
                viewModel.setError("Errore codice $code: ${e.message}")
            }
    }

    error?.let { msg ->
        LaunchedEffect(msg) {
            viewModel.clearError()
        }
        Snackbar(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()
        ) { Text(msg) }
    }

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = { Text("Famiglia", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    if (uiState is FamilyUiState.HasFamily) {
                        IconButton(onClick = { viewModel.signOut() }) {
                            Icon(Icons.Filled.Logout, contentDescription = "Esci", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is FamilyUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is FamilyUiState.NotSignedIn -> {
                    NotSignedInContent(
                        onSignIn = {
                            scope.launch {
                                // revokeAccess svincola l'account a livello GMS (forza il picker)
                                // ma richiede rete — timeout 3s poi fallback a signOut locale
                                val revoked = withTimeoutOrNull(3_000) {
                                    runCatching { googleSignInClient.revokeAccess().await() }.isSuccess
                                }
                                if (revoked == null) {
                                    runCatching { googleSignInClient.signOut().await() }
                                }
                                signInLauncher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    )
                }

                is FamilyUiState.NoFamily -> {
                    NoFamilyContent(
                        userName = state.user.displayName ?: state.user.email ?: "",
                        onCreateFamily = { name -> viewModel.createFamily(name) },
                        onJoinFamily = { code -> viewModel.joinFamily(code) }
                    )
                }

                is FamilyUiState.HasFamily -> {
                    HasFamilyContent(
                        state = state,
                        currentUid = viewModel.currentUser?.uid ?: "",
                        onAddTodo = { title, desc, uid, name, due, important ->
                            viewModel.addTodo(title, desc, uid, name, due, important)
                        },
                        onToggleTodo = { viewModel.toggleTodo(it) },
                        onDeleteTodo = { viewModel.deleteTodo(it) },
                        onLeaveFamily = { viewModel.leaveFamily() },
                        onProposeActivity = { title, desc, type, date, time, dur, cal, uid, name ->
                            viewModel.proposeActivity(title, desc, type, date, time, dur, cal, uid, name)
                        },
                        onApproveProposal = { viewModel.approveProposal(it) },
                        onRejectProposal  = { viewModel.rejectProposal(it) },
                        onAddExpense     = { title, amount, cat -> viewModel.addExpense(title, amount, cat) },
                        onDeleteExpense  = { viewModel.deleteExpense(it) },
                        onToggleSettle   = { viewModel.toggleSettle(it) }
                    )
                }
            }
        }
    }
}

// ── States ────────────────────────────────────────────────────────────────────

@Composable
private fun NotSignedInContent(onSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Groups,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Famiglia AppFit",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Accedi con Google per creare o unirti a un gruppo famiglia e condividere TODO e promemoria.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Accedi con Google")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoFamilyContent(
    userName: String,
    onCreateFamily: (String) -> Unit,
    onJoinFamily: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        InputDialog(
            title = "Crea gruppo famiglia",
            label = "Nome del gruppo",
            placeholder = "Es. Famiglia Rossi",
            confirmText = "Crea",
            onDismiss = { showCreateDialog = false },
            onConfirm = { onCreateFamily(it); showCreateDialog = false }
        )
    }

    if (showJoinDialog) {
        InputDialog(
            title = "Unisciti a un gruppo",
            label = "Codice invito",
            placeholder = "Es. AB3X9K",
            confirmText = "Unisciti",
            onDismiss = { showJoinDialog = false },
            onConfirm = { onJoinFamily(it); showJoinDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.FamilyRestroom,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Ciao, ${userName.substringBefore(" ")}!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Non fai ancora parte di un gruppo famiglia.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Crea gruppo famiglia")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { showJoinDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Login, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Unisciti con codice")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HasFamilyContent(
    state: FamilyUiState.HasFamily,
    currentUid: String,
    onAddTodo: (String, String, String, String, LocalDate?, Boolean) -> Unit,
    onToggleTodo: (FamilyTodo) -> Unit,
    onDeleteTodo: (String) -> Unit,
    onLeaveFamily: () -> Unit,
    onProposeActivity: (String, String, ActivityType, LocalDate, LocalTime?, Int, Int, String, String) -> Unit,
    onApproveProposal: (FamilyActivityProposal) -> Unit,
    onRejectProposal: (FamilyActivityProposal) -> Unit,
    onAddExpense: (String, Float, String) -> Unit,
    onDeleteExpense: (String) -> Unit,
    onToggleSettle: (SharedExpense) -> Unit
) {
    var showLeaveConfirm by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Attività", "Spese", "Todo")

    if (showLeaveConfirm) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirm = false },
            title = { Text("Lascia gruppo") },
            text = { Text("Sei sicuro di voler lasciare il gruppo ${state.family.name}?") },
            confirmButton = {
                TextButton(onClick = { onLeaveFamily(); showLeaveConfirm = false }) {
                    Text("Lascia", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showLeaveConfirm = false }) { Text("Annulla") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header card (sempre visibile)
        FamilyHeaderCard(
            family = state.family, members = state.members,
            currentUid = currentUid, onLeave = { showLeaveConfirm = true },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Tab row
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                val badge = when (index) {
                    0 -> state.proposals.count { it.assignedToUid == currentUid && it.status == "PENDING" }
                    1 -> state.expenses.count { !it.isSettled }
                    2 -> state.todos.count { !it.isCompleted }
                    else -> 0
                }
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title)
                            if (badge > 0) Badge { Text("$badge") }
                        }
                    }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> ActivitiesTabContent(
                state = state, currentUid = currentUid,
                onProposeActivity = onProposeActivity,
                onApprove = onApproveProposal, onReject = onRejectProposal
            )
            1 -> ExpensesTabContent(
                state = state, currentUid = currentUid,
                onAddExpense = onAddExpense, onDelete = onDeleteExpense, onToggleSettle = onToggleSettle
            )
            2 -> TodoTabContent(
                state = state, currentUid = currentUid,
                onAddTodo = onAddTodo, onToggle = onToggleTodo, onDelete = onDeleteTodo
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivitiesTabContent(
    state: FamilyUiState.HasFamily,
    currentUid: String,
    onProposeActivity: (String, String, ActivityType, LocalDate, LocalTime?, Int, Int, String, String) -> Unit,
    onApprove: (FamilyActivityProposal) -> Unit,
    onReject: (FamilyActivityProposal) -> Unit
) {
    var showProposeDialog by remember { mutableStateOf(false) }

    if (showProposeDialog) {
        ProposeActivityDialog(
            members = state.members.filter { it.uid != currentUid },
            onDismiss = { showProposeDialog = false },
            onConfirm = { title, desc, type, date, time, dur, cal, uid, name ->
                onProposeActivity(title, desc, type, date, time, dur, cal, uid, name)
                showProposeDialog = false
            }
        )
    }

    val incoming = state.proposals.filter { it.assignedToUid == currentUid && it.status == "PENDING" }
    val sent     = state.proposals.filter { it.proposedByUid == currentUid }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedButton(onClick = { showProposeDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Proponi attività a un membro")
            }
        }

        if (incoming.isNotEmpty()) {
            item { Text("Da approvare (${incoming.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
            items(incoming, key = { it.id }) { proposal ->
                ProposalCard(proposal = proposal, currentUid = currentUid, onApprove = { onApprove(proposal) }, onReject = { onReject(proposal) })
            }
        }

        if (sent.isNotEmpty()) {
            item { Text("Inviate da te", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
            items(sent, key = { "sent_${it.id}" }) { proposal ->
                ProposalCard(proposal = proposal, currentUid = currentUid, onApprove = null, onReject = null)
            }
        }

        if (incoming.isEmpty() && sent.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.FitnessCenter, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Spacer(Modifier.height(8.dp))
                        Text("Nessuna proposta di attività", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpensesTabContent(
    state: FamilyUiState.HasFamily,
    currentUid: String,
    onAddExpense: (String, Float, String) -> Unit,
    onDelete: (String) -> Unit,
    onToggleSettle: (SharedExpense) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, category -> onAddExpense(title, amount, category); showAddDialog = false }
        )
    }

    val totalUnsettled = state.expenses.filter { !it.isSettled }.sumOf { it.amount.toDouble() }.toFloat()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.expenses.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Totale da saldare", style = MaterialTheme.typography.bodyMedium)
                        Text("€${"%.2f".format(totalUnsettled)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            OutlinedButton(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Aggiungi spesa")
            }
        }

        if (state.expenses.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Payments, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Spacer(Modifier.height(8.dp))
                        Text("Nessuna spesa condivisa", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(state.expenses, key = { it.id }) { expense ->
                ExpenseCard(expense = expense, currentUid = currentUid, onToggleSettle = { onToggleSettle(expense) }, onDelete = { onDelete(expense.id) })
            }
        }
    }
}

@Composable
private fun TodoTabContent(
    state: FamilyUiState.HasFamily,
    currentUid: String,
    onAddTodo: (String, String, String, String, LocalDate?, Boolean) -> Unit,
    onToggle: (FamilyTodo) -> Unit,
    onDelete: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTodoDialog(
            members = state.members, currentUid = currentUid,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, uid, name, due, important ->
                onAddTodo(title, desc, uid, name, due, important)
                showAddDialog = false
            }
        )
    }

    val pending   = state.todos.filter { !it.isCompleted }
    val completed = state.todos.filter { it.isCompleted }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedButton(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Aggiungi TODO")
            }
        }

        if (pending.isNotEmpty()) {
            item { Text("Da fare (${pending.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
            items(pending, key = { it.id }) { todo ->
                TodoCard(todo = todo, currentUid = currentUid, onToggle = { onToggle(todo) }, onDelete = { onDelete(todo.id) })
            }
        }

        if (completed.isNotEmpty()) {
            item {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Completati (${completed.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showCompleted = !showCompleted }) { Text(if (showCompleted) "Nascondi" else "Mostra") }
                }
            }
            if (showCompleted) {
                items(completed, key = { "done_${it.id}" }) { todo ->
                    TodoCard(todo = todo, currentUid = currentUid, onToggle = { onToggle(todo) }, onDelete = { onDelete(todo.id) })
                }
            }
        }

        if (pending.isEmpty() && completed.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("Nessun TODO! Ottimo lavoro di squadra.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ── Cards ─────────────────────────────────────────────────────────────────────

@Composable
private fun FamilyHeaderCard(
    family: com.appfit.data.model.Family,
    members: List<FamilyMember>,
    currentUid: String,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        family.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                TextButton(onClick = onLeave) {
                    Text("Lascia", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Key,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Codice: ${family.inviteCode}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Membri",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                members.forEach { member ->
                    MemberAvatar(member = member, isCurrentUser = member.uid == currentUid)
                }
            }
        }
    }
}

@Composable
private fun MemberAvatar(member: FamilyMember, isCurrentUser: Boolean) {
    val initial = member.name.firstOrNull()?.uppercaseChar() ?: '?'
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isCurrentUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                initial.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            member.name.substringBefore(" "),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 48.dp)
        )
    }
}

@Composable
private fun TodoCard(
    todo: FamilyTodo,
    currentUid: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isOverdue = todo.dueDate?.let {
        val dueLocal = it.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        dueLocal.isBefore(LocalDate.now()) && !todo.isCompleted
    } ?: false

    val containerColor = when {
        todo.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        isOverdue -> MaterialTheme.colorScheme.errorContainer
        todo.isImportant -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (todo.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (todo.isImportant && !todo.isCompleted) {
                        Icon(
                            Icons.Filled.PriorityHigh,
                            contentDescription = "Importante",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        todo.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (todo.isImportant && !todo.isCompleted) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (todo.description.isNotBlank()) {
                    Text(
                        todo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    if (todo.assignedToName.isNotBlank()) {
                        AssigneeChip(
                            name = todo.assignedToName,
                            isCurrentUser = todo.assignedToUid == currentUid
                        )
                    }
                    todo.dueDate?.let { ts ->
                        val local = ts.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        val label = formatDueDate(local)
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        )
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.DeleteOutline,
                    contentDescription = "Elimina",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AssigneeChip(name: String, isCurrentUser: Boolean) {
    SuggestionChip(
        onClick = {},
        label = {
            Text(
                if (isCurrentUser) "Tu" else name.substringBefore(" "),
                style = MaterialTheme.typography.labelSmall
            )
        },
        icon = {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
        }
    )
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@Composable
private fun InputDialog(
    title: String,
    label: String,
    placeholder: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text.trim()) },
                enabled = text.isNotBlank()
            ) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTodoDialog(
    members: List<FamilyMember>,
    currentUid: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, LocalDate?, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMemberIndex by remember {
        mutableStateOf(members.indexOfFirst { it.uid == currentUid }.takeIf { it >= 0 } ?: 0)
    }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var isImportant by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dueDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo TODO") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titolo *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                if (members.isNotEmpty()) {
                    Text("Assegna a:", style = MaterialTheme.typography.labelMedium)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        members.forEachIndexed { idx, member ->
                            SegmentedButton(
                                selected = selectedMemberIndex == idx,
                                onClick = { selectedMemberIndex = idx },
                                shape = SegmentedButtonDefaults.itemShape(idx, members.size)
                            ) {
                                Text(
                                    if (member.uid == currentUid) "Tu" else member.name.substringBefore(" "),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(dueDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Scadenza (opzionale)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isImportant, onCheckedChange = { isImportant = it })
                    Text("Importante (notifica ripetuta fino al completamento)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && members.isNotEmpty()) {
                        val member = members[selectedMemberIndex]
                        onConfirm(title.trim(), description.trim(), member.uid, member.name, dueDate, isImportant)
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

// ── Proposal Card ─────────────────────────────────────────────────────────────

@Composable
private fun ProposalCard(
    proposal: FamilyActivityProposal,
    currentUid: String,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?
) {
    val localDate = remember(proposal.scheduledDate) {
        proposal.scheduledDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val isIncoming = proposal.assignedToUid == currentUid
    val statusColor = when (proposal.status) {
        "APPROVED" -> MaterialTheme.colorScheme.primary
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusLabel = when (proposal.status) {
        "APPROVED" -> "✓ Approvata"
        "REJECTED" -> "✗ Rifiutata"
        else -> "⏳ In attesa"
    }

    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(proposal.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (isIncoming) "Da: ${proposal.proposedByName.substringBefore(" ")}"
                        else "Per: ${proposal.assignedToName.substringBefore(" ")}",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(statusLabel, style = MaterialTheme.typography.labelSmall, color = statusColor)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "📅 ${localDate.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", java.util.Locale.ITALIAN))}" +
                        (if (proposal.scheduledTime.isNotBlank()) " · ⏰ ${proposal.scheduledTime}" else "") +
                        " · ${proposal.durationMinutes}min",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary
            )
            if (proposal.description.isNotBlank()) {
                Text(proposal.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
            if (isIncoming && proposal.status == "PENDING" && onApprove != null && onReject != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Rifiuta") }
                    Button(onClick = onApprove, modifier = Modifier.weight(1f)) { Text("Approva → Calendario") }
                }
            }
        }
    }
}

// ── Expense Card ──────────────────────────────────────────────────────────────

@Composable
private fun ExpenseCard(expense: SharedExpense, currentUid: String, onToggleSettle: () -> Unit, onDelete: () -> Unit) {
    val isMine = expense.addedByUid == currentUid
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (expense.isSettled) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(expense.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
                    textDecoration = if (expense.isSettled) TextDecoration.LineThrough else null)
                Text(
                    "${expense.category} · ${if (isMine) "Tu" else expense.addedByName.substringBefore(" ")}",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("€${"%.2f".format(expense.amount)}", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (expense.isSettled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onToggleSettle, modifier = Modifier.size(32.dp)) {
                Icon(if (expense.isSettled) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = "Salda", modifier = Modifier.size(20.dp),
                    tint = if (expense.isSettled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Elimina", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ── ProposeActivityDialog ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProposeActivityDialog(
    members: List<FamilyMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, ActivityType, LocalDate, LocalTime?, Int, Int, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ActivityType.CARDIO) }
    var selectedMemberIdx by remember { mutableIntStateOf(0) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var timeInput by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("60") }
    var calories by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Annulla") } }
        ) { DatePicker(state = dpState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Proponi attività") },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titolo *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrizione") }, modifier = Modifier.fillMaxWidth(), maxLines = 2)

                Text("Tipo", style = MaterialTheme.typography.labelMedium)
                val types = ActivityType.entries
                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = selectedType.displayName(), onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        label = { Text("Tipo attività") }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        types.forEach { t ->
                            DropdownMenuItem(text = { Text(t.displayName()) }, onClick = { selectedType = t; expanded = false })
                        }
                    }
                }

                if (members.isNotEmpty()) {
                    Text("Per chi:", style = MaterialTheme.typography.labelMedium)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        members.forEachIndexed { idx, m ->
                            SegmentedButton(selected = selectedMemberIdx == idx, onClick = { selectedMemberIdx = idx },
                                shape = SegmentedButtonDefaults.itemShape(idx, members.size)
                            ) { Text(m.name.substringBefore(" "), maxLines = 1) }
                        }
                    }
                }

                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("📅 ${date.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", java.util.Locale.ITALIAN))}")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = timeInput, onValueChange = { timeInput = it },
                        label = { Text("Orario (HH:mm)") }, modifier = Modifier.weight(1f), singleLine = true,
                        placeholder = { Text("es. 09:30") })
                    OutlinedTextField(value = duration, onValueChange = { duration = it },
                        label = { Text("Durata (min)") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                OutlinedTextField(value = calories, onValueChange = { calories = it },
                    label = { Text("Calorie stimate") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && members.isNotEmpty()) {
                        val member = members[selectedMemberIdx]
                        val localTime = runCatching { LocalTime.parse(timeInput) }.getOrNull()
                        onConfirm(title.trim(), description.trim(), selectedType, date, localTime,
                            duration.toIntOrNull() ?: 60, calories.toIntOrNull() ?: 0, member.uid, member.name)
                    }
                },
                enabled = title.isNotBlank() && members.isNotEmpty()
            ) { Text("Proponi") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}

// ── AddExpenseDialog ──────────────────────────────────────────────────────────

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float, String) -> Unit
) {
    val categories = listOf("Spesa", "Ristorante", "Sport", "Casa", "Trasporti", "Altro")
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("Altro") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi spesa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titolo *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Importo (€) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), placeholder = { Text("es. 12.50") })
                Text("Categoria", style = MaterialTheme.typography.labelMedium)
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(selected = selectedCat == cat, onClick = { selectedCat = cat }, label = { Text(cat) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { amount.toFloatOrNull()?.let { onConfirm(title.trim(), it, selectedCat) } },
                enabled = title.isNotBlank() && amount.toFloatOrNull() != null
            ) { Text("Aggiungi") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatDueDate(date: LocalDate): String {
    val today = LocalDate.now()
    return when {
        date == today -> "Oggi"
        date == today.plusDays(1) -> "Domani"
        date.isBefore(today) -> {
            val days = today.toEpochDay() - date.toEpochDay()
            "Scaduto ${days}gg fa"
        }
        else -> date.format(DateTimeFormatter.ofPattern("dd/MM"))
    }
}


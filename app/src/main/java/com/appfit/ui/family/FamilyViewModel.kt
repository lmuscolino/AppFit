package com.appfit.ui.family

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfit.ai.GoogleCalendarService
import com.appfit.data.model.*
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.FamilyRepository
import com.appfit.data.repository.ReminderRepository
import com.appfit.notification.FamilyReminderWorker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val repo: FamilyRepository,
    private val activityRepository: ActivityRepository,
    private val reminderRepository: ReminderRepository,
    private val googleCalendarService: GoogleCalendarService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<FamilyUiState>(FamilyUiState.Loading)
    val uiState: StateFlow<FamilyUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val currentUser: FirebaseUser? get() = repo.currentUser

    init { checkState() }

    fun checkState() {
        viewModelScope.launch {
            val user = repo.currentUser
            if (user == null) { _uiState.value = FamilyUiState.NotSignedIn; return@launch }
            _uiState.value = FamilyUiState.Loading
            val familyId = runCatching { repo.getUserFamilyId() }.getOrNull()
            if (familyId == null) { _uiState.value = FamilyUiState.NoFamily(user); return@launch }
            val family = runCatching { repo.getFamily(familyId) }.getOrNull()
            if (family == null) { _uiState.value = FamilyUiState.NoFamily(user); return@launch }
            observeFamily(family, user)
        }
    }

    private fun observeFamily(family: Family, user: FirebaseUser) {
        FamilyReminderWorker.schedule(context)
        val todosFlow     = repo.todosFlow(family.id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        val membersFlow   = repo.membersFlow(family.id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        val proposalsFlow = repo.activityProposalsFlow(family.id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        val expensesFlow  = repo.expensesFlow(family.id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            combine(todosFlow, membersFlow, proposalsFlow, expensesFlow) { todos, members, proposals, expenses ->
                FamilyUiState.HasFamily(
                    user = user, family = family,
                    todos = todos, members = members,
                    proposals = proposals, expenses = expenses
                )
            }.collect { _uiState.value = it }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            runCatching { repo.signInWithGoogle(idToken) }
                .onSuccess { checkState() }
                .onFailure { _error.value = "Accesso fallito: ${it.message}" }
        }
    }

    fun signOut() {
        repo.signOut()
        FamilyReminderWorker.cancel(context)
        _uiState.value = FamilyUiState.NotSignedIn
    }

    fun createFamily(name: String) {
        val user = repo.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = FamilyUiState.Loading
            runCatching { repo.createFamily(name) }
                .onSuccess { checkState() }
                .onFailure {
                    _error.value = "Errore creazione: ${it.message}"
                    _uiState.value = FamilyUiState.NoFamily(user)
                }
        }
    }

    fun joinFamily(code: String) {
        val user = repo.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = FamilyUiState.Loading
            runCatching { repo.joinFamily(code) }
                .onSuccess { checkState() }
                .onFailure {
                    _error.value = it.message ?: "Codice non valido"
                    _uiState.value = FamilyUiState.NoFamily(user)
                }
        }
    }

    fun leaveFamily() {
        val state = _uiState.value as? FamilyUiState.HasFamily ?: return
        viewModelScope.launch {
            runCatching { repo.leaveFamily(state.family.id) }
                .onSuccess { FamilyReminderWorker.cancel(context); _uiState.value = FamilyUiState.NoFamily(state.user) }
                .onFailure { _error.value = "Errore: ${it.message}" }
        }
    }

    // ── Todo ─────────────────────────────────────────────────────────────────

    fun addTodo(title: String, description: String, assignedToUid: String, assignedToName: String, dueDate: LocalDate?, isImportant: Boolean) {
        val familyId = (_uiState.value as? FamilyUiState.HasFamily)?.family?.id ?: return
        viewModelScope.launch {
            runCatching { repo.addTodo(familyId, title, description, assignedToUid, assignedToName, dueDate, isImportant) }
                .onFailure { _error.value = "Errore aggiunta: ${it.message}" }
        }
    }

    fun toggleTodo(todo: FamilyTodo) {
        val familyId = (_uiState.value as? FamilyUiState.HasFamily)?.family?.id ?: return
        viewModelScope.launch { runCatching { repo.setTodoCompleted(familyId, todo.id, !todo.isCompleted) } }
    }

    fun deleteTodo(todoId: String) {
        val familyId = (_uiState.value as? FamilyUiState.HasFamily)?.family?.id ?: return
        viewModelScope.launch { runCatching { repo.deleteTodo(familyId, todoId) } }
    }

    // ── Activity Proposals ────────────────────────────────────────────────────

    fun proposeActivity(
        title: String, description: String, activityType: ActivityType,
        date: LocalDate, time: LocalTime?, durationMinutes: Int, caloriesBurned: Int,
        assignedToUid: String, assignedToName: String
    ) {
        val state = _uiState.value as? FamilyUiState.HasFamily ?: return
        val user = repo.currentUser ?: return
        viewModelScope.launch {
            val ts = Timestamp(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            val proposal = FamilyActivityProposal(
                title = title, description = description,
                activityType = activityType.name,
                scheduledDate = ts,
                scheduledTime = time?.toString() ?: "",
                durationMinutes = durationMinutes, caloriesBurned = caloriesBurned,
                proposedByUid = user.uid,
                proposedByName = user.displayName ?: user.email ?: "",
                assignedToUid = assignedToUid, assignedToName = assignedToName,
                status = "PENDING"
            )
            runCatching { repo.proposeActivity(state.family.id, proposal) }
                .onFailure { _error.value = "Errore proposta: ${it.message}" }
        }
    }

    fun approveProposal(proposal: FamilyActivityProposal) {
        val state = _uiState.value as? FamilyUiState.HasFamily ?: return
        viewModelScope.launch {
            try {
                val localDate = proposal.scheduledDate.toDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val localTime = proposal.scheduledTime.takeIf { it.isNotBlank() }
                    ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                val type = runCatching { ActivityType.valueOf(proposal.activityType) }
                    .getOrDefault(ActivityType.CUSTOM)

                val activity = Activity(
                    title = proposal.title,
                    description = "📋 Proposta da ${proposal.proposedByName}\n${proposal.description}".trim(),
                    type = type,
                    durationMinutes = proposal.durationMinutes,
                    scheduledDate = localDate,
                    scheduledTime = localTime,
                    caloriesBurned = proposal.caloriesBurned,
                    aiGenerated = false
                )

                // 1. Salva nel DB locale → appare in Calendario e Allenamenti
                val id = activityRepository.insertActivity(activity)
                Log.d("Family", "approveProposal: insertActivity id=$id '${activity.title}' il $localDate")

                // 2. Crea promemoria nella sezione Promemoria
                val reminder = Reminder(
                    title = proposal.title,
                    description = "Attività proposta da ${proposal.proposedByName}",
                    category = ReminderCategory.HEALTH,
                    dueDate = localDate,
                    isImportant = true
                )
                runCatching { reminderRepository.insertReminder(reminder) }

                // 3. Sync Google Calendar (best-effort)
                runCatching { googleCalendarService.syncActivity(activity.copy(id = id)) }

                // 4. Aggiorna stato su Firestore (best-effort — non blocca il salvataggio locale)
                runCatching { repo.updateProposalStatus(state.family.id, proposal.id, "APPROVED") }
                    .onFailure { Log.w("Family", "updateProposalStatus failed: ${it.message}") }

            } catch (e: Exception) {
                Log.e("Family", "approveProposal error: ${e.message}", e)
                _error.value = "Errore approvazione: ${e.message}"
            }
        }
    }

    fun rejectProposal(proposal: FamilyActivityProposal) {
        val familyId = (_uiState.value as? FamilyUiState.HasFamily)?.family?.id ?: return
        viewModelScope.launch {
            runCatching { repo.updateProposalStatus(familyId, proposal.id, "REJECTED") }
        }
    }

    // ── Shared Expenses ───────────────────────────────────────────────────────

    fun addExpense(title: String, amount: Float, category: String) {
        val state = _uiState.value as? FamilyUiState.HasFamily ?: return
        val user = repo.currentUser ?: return
        viewModelScope.launch {
            val expense = SharedExpense(
                title = title, amount = amount, category = category,
                addedByUid = user.uid,
                addedByName = user.displayName ?: user.email ?: ""
            )
            runCatching { repo.addExpense(state.family.id, expense) }
                .onFailure { _error.value = "Errore aggiunta spesa: ${it.message}" }
        }
    }

    fun deleteExpense(expenseId: String) {
        val familyId = (_uiState.value as? FamilyUiState.HasFamily)?.family?.id ?: return
        viewModelScope.launch { runCatching { repo.deleteExpense(familyId, expenseId) } }
    }

    fun toggleSettle(expense: SharedExpense) {
        val familyId = (_uiState.value as? FamilyUiState.HasFamily)?.family?.id ?: return
        viewModelScope.launch {
            runCatching { repo.toggleSettleExpense(familyId, expense.id, !expense.isSettled) }
        }
    }

    fun clearError() { _error.value = null }
    fun setError(msg: String) { _error.value = msg }
}

sealed class FamilyUiState {
    object Loading : FamilyUiState()
    object NotSignedIn : FamilyUiState()
    data class NoFamily(val user: FirebaseUser) : FamilyUiState()
    data class HasFamily(
        val user: FirebaseUser,
        val family: Family,
        val todos: List<FamilyTodo>,
        val members: List<FamilyMember>,
        val proposals: List<FamilyActivityProposal> = emptyList(),
        val expenses: List<SharedExpense> = emptyList()
    ) : FamilyUiState()
}

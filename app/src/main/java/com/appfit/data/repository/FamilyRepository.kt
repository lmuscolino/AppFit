package com.appfit.data.repository

import com.appfit.data.model.Family
import com.appfit.data.model.FamilyActivityProposal
import com.appfit.data.model.FamilyMember
import com.appfit.data.model.FamilyTodo
import com.appfit.data.model.SharedExpense
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user!!
        // Ensure user doc exists — se Firestore nega l'accesso (regole non configurate)
        // ignoriamo l'errore: l'auth Firebase è già avvenuta con successo
        runCatching {
            val userRef = db.collection("users").document(user.uid)
            val snap = userRef.get().await()
            if (!snap.exists()) {
                userRef.set(mapOf(
                    "name" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "familyId" to null
                )).await()
            }
        }
        return user
    }

    fun signOut() = auth.signOut()

    // ── Family ────────────────────────────────────────────────────────────────

    suspend fun getUserFamilyId(): String? {
        val uid = auth.currentUser?.uid ?: return null
        return runCatching {
            db.collection("users").document(uid).get().await().getString("familyId")
        }.getOrNull()
    }

    suspend fun createFamily(familyName: String): Family {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val displayName = auth.currentUser?.displayName ?: ""
        val code = generateInviteCode()

        val familyRef = db.collection("families").document()
        val family = Family(
            id = familyRef.id,
            name = familyName,
            inviteCode = code,
            adminUid = uid
        )
        familyRef.set(mapOf(
            "name" to family.name,
            "inviteCode" to family.inviteCode,
            "adminUid" to family.adminUid
        )).await()

        // Add admin as member
        familyRef.collection("members").document(uid).set(mapOf(
            "uid" to uid,
            "name" to displayName,
            "email" to (auth.currentUser?.email ?: ""),
            "role" to "admin"
        )).await()

        // Link user to family (set con merge crea il doc se non esiste)
        db.collection("users").document(uid)
            .set(mapOf("name" to displayName, "email" to (auth.currentUser?.email ?: ""), "familyId" to familyRef.id),
                com.google.firebase.firestore.SetOptions.merge()).await()

        return family
    }

    suspend fun joinFamily(inviteCode: String): Family {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val displayName = auth.currentUser?.displayName ?: ""

        val snap = db.collection("families")
            .whereEqualTo("inviteCode", inviteCode.uppercase())
            .get().await()

        if (snap.isEmpty) error("Codice non valido")

        val doc = snap.documents.first()
        val familyId = doc.id
        val familyName = doc.getString("name") ?: ""

        // Add as member
        doc.reference.collection("members").document(uid).set(mapOf(
            "uid" to uid,
            "name" to displayName,
            "email" to (auth.currentUser?.email ?: ""),
            "role" to "member"
        )).await()

        // Link user (set con merge crea il doc se non esiste)
        db.collection("users").document(uid)
            .set(mapOf("name" to displayName, "email" to (auth.currentUser?.email ?: ""), "familyId" to familyId),
                com.google.firebase.firestore.SetOptions.merge()).await()

        return Family(
            id = familyId,
            name = familyName,
            inviteCode = doc.getString("inviteCode") ?: "",
            adminUid = doc.getString("adminUid") ?: ""
        )
    }

    suspend fun getFamily(familyId: String): Family? {
        val snap = db.collection("families").document(familyId).get().await()
        if (!snap.exists()) return null
        return Family(
            id = snap.id,
            name = snap.getString("name") ?: "",
            inviteCode = snap.getString("inviteCode") ?: "",
            adminUid = snap.getString("adminUid") ?: ""
        )
    }

    suspend fun leaveFamily(familyId: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("families").document(familyId)
            .collection("members").document(uid).delete().await()
        db.collection("users").document(uid)
            .set(mapOf("familyId" to null), com.google.firebase.firestore.SetOptions.merge()).await()
    }

    // ── Members ───────────────────────────────────────────────────────────────

    fun membersFlow(familyId: String): Flow<List<FamilyMember>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("families").document(familyId)
            .collection("members")
            .addSnapshotListener { snap, _ ->
                val members = snap?.documents?.map { doc ->
                    FamilyMember(
                        uid = doc.getString("uid") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: "member"
                    )
                } ?: emptyList()
                trySend(members)
            }
        awaitClose { reg.remove() }
    }

    // ── Todos ─────────────────────────────────────────────────────────────────

    fun todosFlow(familyId: String): Flow<List<FamilyTodo>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("families").document(familyId)
            .collection("todos")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val todos = snap?.documents?.mapNotNull { doc ->
                    runCatching {
                        FamilyTodo(
                            id = doc.id,
                            title = doc.getString("title") ?: return@mapNotNull null,
                            description = doc.getString("description") ?: "",
                            assignedToUid = doc.getString("assignedToUid") ?: "",
                            assignedToName = doc.getString("assignedToName") ?: "",
                            createdByUid = doc.getString("createdByUid") ?: "",
                            createdByName = doc.getString("createdByName") ?: "",
                            dueDate = doc.getTimestamp("dueDate"),
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            isImportant = doc.getBoolean("isImportant") ?: false,
                            createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                        )
                    }.getOrNull()
                } ?: emptyList()
                trySend(todos)
            }
        awaitClose { reg.remove() }
    }

    suspend fun addTodo(
        familyId: String,
        title: String,
        description: String,
        assignedToUid: String,
        assignedToName: String,
        dueDate: LocalDate?,
        isImportant: Boolean
    ) {
        val uid = auth.currentUser?.uid ?: return
        val name = auth.currentUser?.displayName ?: ""
        val dueDateTs = dueDate?.let {
            val instant = it.atStartOfDay(ZoneId.systemDefault()).toInstant()
            Timestamp(Date.from(instant))
        }
        db.collection("families").document(familyId)
            .collection("todos")
            .add(mapOf(
                "title" to title,
                "description" to description,
                "assignedToUid" to assignedToUid,
                "assignedToName" to assignedToName,
                "createdByUid" to uid,
                "createdByName" to name,
                "dueDate" to dueDateTs,
                "isCompleted" to false,
                "isImportant" to isImportant,
                "createdAt" to Timestamp.now()
            )).await()
    }

    suspend fun setTodoCompleted(familyId: String, todoId: String, completed: Boolean) {
        db.collection("families").document(familyId)
            .collection("todos").document(todoId)
            .update("isCompleted", completed).await()
    }

    suspend fun deleteTodo(familyId: String, todoId: String) {
        db.collection("families").document(familyId)
            .collection("todos").document(todoId)
            .delete().await()
    }

    suspend fun getImportantPendingTodosForUser(familyId: String, uid: String): List<FamilyTodo> {
        val snap = db.collection("families").document(familyId)
            .collection("todos")
            .whereEqualTo("assignedToUid", uid)
            .whereEqualTo("isImportant", true)
            .whereEqualTo("isCompleted", false)
            .get().await()
        return snap.documents.mapNotNull { doc ->
            runCatching {
                FamilyTodo(
                    id = doc.id,
                    title = doc.getString("title") ?: return@mapNotNull null,
                    isImportant = true,
                    isCompleted = false,
                    assignedToUid = uid,
                    assignedToName = doc.getString("assignedToName") ?: ""
                )
            }.getOrNull()
        }
    }

    suspend fun getPendingProposalsForUser(familyId: String, uid: String): List<FamilyActivityProposal> {
        val snap = db.collection("families").document(familyId)
            .collection("activityProposals")
            .whereEqualTo("assignedToUid", uid)
            .whereEqualTo("status", "PENDING")
            .get().await()
        return snap.documents.mapNotNull { doc ->
            runCatching {
                FamilyActivityProposal(
                    id = doc.id,
                    title = doc.getString("title") ?: return@mapNotNull null,
                    description = doc.getString("description") ?: "",
                    activityType = doc.getString("activityType") ?: "CUSTOM",
                    scheduledDate = doc.getTimestamp("scheduledDate") ?: Timestamp.now(),
                    scheduledTime = doc.getString("scheduledTime") ?: "",
                    durationMinutes = (doc.getLong("durationMinutes") ?: 60L).toInt(),
                    caloriesBurned = (doc.getLong("caloriesBurned") ?: 0L).toInt(),
                    proposedByUid = doc.getString("proposedByUid") ?: "",
                    proposedByName = doc.getString("proposedByName") ?: "",
                    assignedToUid = uid,
                    assignedToName = doc.getString("assignedToName") ?: "",
                    status = "PENDING",
                    createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                )
            }.getOrNull()
        }
    }

    // ── Activity Proposals ────────────────────────────────────────────────────

    fun activityProposalsFlow(familyId: String): Flow<List<FamilyActivityProposal>> = callbackFlow {
        val reg = db.collection("families").document(familyId)
            .collection("activityProposals")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    runCatching {
                        FamilyActivityProposal(
                            id = doc.id,
                            title = doc.getString("title") ?: return@mapNotNull null,
                            description = doc.getString("description") ?: "",
                            activityType = doc.getString("activityType") ?: "CUSTOM",
                            scheduledDate = doc.getTimestamp("scheduledDate") ?: Timestamp.now(),
                            scheduledTime = doc.getString("scheduledTime") ?: "",
                            durationMinutes = (doc.getLong("durationMinutes") ?: 60L).toInt(),
                            caloriesBurned = (doc.getLong("caloriesBurned") ?: 0L).toInt(),
                            proposedByUid = doc.getString("proposedByUid") ?: "",
                            proposedByName = doc.getString("proposedByName") ?: "",
                            assignedToUid = doc.getString("assignedToUid") ?: "",
                            assignedToName = doc.getString("assignedToName") ?: "",
                            status = doc.getString("status") ?: "PENDING",
                            createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                        )
                    }.getOrNull()
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun proposeActivity(familyId: String, proposal: FamilyActivityProposal) {
        db.collection("families").document(familyId)
            .collection("activityProposals")
            .add(mapOf(
                "title" to proposal.title,
                "description" to proposal.description,
                "activityType" to proposal.activityType,
                "scheduledDate" to proposal.scheduledDate,
                "scheduledTime" to proposal.scheduledTime,
                "durationMinutes" to proposal.durationMinutes,
                "caloriesBurned" to proposal.caloriesBurned,
                "proposedByUid" to proposal.proposedByUid,
                "proposedByName" to proposal.proposedByName,
                "assignedToUid" to proposal.assignedToUid,
                "assignedToName" to proposal.assignedToName,
                "status" to "PENDING",
                "createdAt" to Timestamp.now()
            )).await()
    }

    suspend fun updateProposalStatus(familyId: String, proposalId: String, status: String) {
        db.collection("families").document(familyId)
            .collection("activityProposals").document(proposalId)
            .update("status", status).await()
    }

    // ── Shared Expenses ───────────────────────────────────────────────────────

    fun expensesFlow(familyId: String): Flow<List<SharedExpense>> = callbackFlow {
        val reg = db.collection("families").document(familyId)
            .collection("expenses")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    runCatching {
                        SharedExpense(
                            id = doc.id,
                            title = doc.getString("title") ?: return@mapNotNull null,
                            amount = (doc.getDouble("amount") ?: 0.0).toFloat(),
                            category = doc.getString("category") ?: "Altro",
                            addedByUid = doc.getString("addedByUid") ?: "",
                            addedByName = doc.getString("addedByName") ?: "",
                            date = doc.getTimestamp("date") ?: Timestamp.now(),
                            isSettled = doc.getBoolean("isSettled") ?: false,
                            createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                        )
                    }.getOrNull()
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun addExpense(familyId: String, expense: SharedExpense) {
        db.collection("families").document(familyId)
            .collection("expenses")
            .add(mapOf(
                "title" to expense.title,
                "amount" to expense.amount.toDouble(),
                "category" to expense.category,
                "addedByUid" to expense.addedByUid,
                "addedByName" to expense.addedByName,
                "date" to Timestamp.now(),
                "isSettled" to false,
                "createdAt" to Timestamp.now()
            )).await()
    }

    suspend fun deleteExpense(familyId: String, expenseId: String) {
        db.collection("families").document(familyId)
            .collection("expenses").document(expenseId).delete().await()
    }

    suspend fun toggleSettleExpense(familyId: String, expenseId: String, isSettled: Boolean) {
        db.collection("families").document(familyId)
            .collection("expenses").document(expenseId)
            .update("isSettled", isSettled).await()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}

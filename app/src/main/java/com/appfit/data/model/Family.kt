package com.appfit.data.model

import com.google.firebase.Timestamp

data class FamilyActivityProposal(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val activityType: String = "CUSTOM",
    val scheduledDate: Timestamp = Timestamp.now(),
    val scheduledTime: String = "",
    val durationMinutes: Int = 60,
    val caloriesBurned: Int = 0,
    val proposedByUid: String = "",
    val proposedByName: String = "",
    val assignedToUid: String = "",
    val assignedToName: String = "",
    val status: String = "PENDING",
    val createdAt: Timestamp = Timestamp.now()
)

data class SharedExpense(
    val id: String = "",
    val title: String = "",
    val amount: Float = 0f,
    val category: String = "Altro",
    val addedByUid: String = "",
    val addedByName: String = "",
    val date: Timestamp = Timestamp.now(),
    val isSettled: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

data class Family(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val adminUid: String = ""
)

data class FamilyMember(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "member"  // "admin" | "member"
)

data class FamilyTodo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val assignedToUid: String = "",
    val assignedToName: String = "",
    val createdByUid: String = "",
    val createdByName: String = "",
    val dueDate: Timestamp? = null,
    val isCompleted: Boolean = false,
    val isImportant: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

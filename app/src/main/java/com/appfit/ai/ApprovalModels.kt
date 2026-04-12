package com.appfit.ai

enum class DuplicateAction { REPLACE, ADD_NEW }

data class ApprovalItem(
    val toolUseId: String,
    val toolName: String,
    val inputJson: String,
    val displayTitle: String,
    val displayDetail: String,
    val isDeleteAction: Boolean = false,
    val hasDuplicate: Boolean = false,
    val duplicateId: Long? = null,
    val duplicateName: String? = null,
    val isSelected: Boolean = true,
    val selectedDuplicateAction: DuplicateAction = DuplicateAction.REPLACE
)

data class ApprovalRequest(val items: List<ApprovalItem>)

sealed class ApprovalResult {
    data class Approved(val items: List<ApprovalItem>) : ApprovalResult()
    object Rejected : ApprovalResult()
}

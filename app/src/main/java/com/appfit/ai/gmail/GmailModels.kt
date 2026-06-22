package com.appfit.ai.gmail

import com.appfit.data.model.PendingItemType

data class EmailMeta(
    val id: String,
    val subject: String,
    val from: String,
    val date: String,
    val snippet: String
)

data class AnalysisResult(
    val emailMeta: EmailMeta,
    val type: PendingItemType,
    val reason: String
)

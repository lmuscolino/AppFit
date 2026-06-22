package com.appfit.ai.gmail

import com.appfit.data.model.PendingEmailItem
import com.appfit.data.model.PendingItemType
import javax.inject.Inject
import javax.inject.Singleton

sealed class ScanResult {
    data class Done(val newItems: List<PendingEmailItem>, val scannedCount: Int) : ScanResult()
    object ApiError : ScanResult()
    object AiUnavailable : ScanResult()
}

@Singleton
class GmailScanService @Inject constructor(
    private val apiClient: GmailApiClient,
    private val aiAnalyzer: GmailAiAnalyzer
) {
    fun scan(
        token: String,
        lastScanMs: Long,
        alreadyProcessed: Set<String>,
        geminiKey: String?
    ): ScanResult {
        if (geminiKey == null) {
            println("[GmailScanService] nessuna AI key disponibile")
            return ScanResult.AiUnavailable
        }

        val sinceEpochSec = if (lastScanMs > 0) lastScanMs / 1000
                            else System.currentTimeMillis() / 1000 - 7 * 86400L

        val ids = apiClient.listMessageIds(token, sinceEpochSec) ?: return ScanResult.ApiError
        println("[GmailScanService] trovati ${ids.size} messaggi, già elaborati=${alreadyProcessed.size}")

        val newIds = ids.filter { it !in alreadyProcessed }
        if (newIds.isEmpty()) {
            println("[GmailScanService] nessun messaggio nuovo")
            return ScanResult.Done(emptyList(), 0)
        }

        val emails = newIds.mapNotNull { apiClient.fetchMeta(token, it) }
        println("[GmailScanService] metadata recuperati=${emails.size}")

        val interesting = aiAnalyzer.phase1Filter(emails, geminiKey)
            ?: return ScanResult.Done(emptyList(), emails.size) // AI error → non bloccare
        println("[GmailScanService] email interessanti=${interesting.size}")

        val items = interesting.mapNotNull { result ->
            val body = apiClient.fetchBody(token, result.emailMeta.id) ?: return@mapNotNull null
            val payload = aiAnalyzer.phase2Extract(result.emailMeta, body, result.type, geminiKey)
                ?: return@mapNotNull null
            PendingEmailItem(
                sourceEmailId      = result.emailMeta.id,
                sourceEmailSubject = result.emailMeta.subject,
                sourceEmailFrom    = result.emailMeta.from,
                itemType           = result.type,
                title              = payload.get("title")?.asString ?: result.emailMeta.subject,
                payloadJson        = payload.toString(),
                aiReason           = result.reason
            )
        }

        return ScanResult.Done(items, emails.size)
    }
}

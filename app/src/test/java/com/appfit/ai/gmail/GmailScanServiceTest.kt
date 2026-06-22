package com.appfit.ai.gmail

import com.appfit.data.model.PendingItemType
import com.google.gson.JsonObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GmailScanServiceTest {

    // Implementazioni fake per i test — non servono mock framework
    private lateinit var fakeApiClient: FakeGmailApiClient
    private lateinit var fakeAnalyzer: FakeGmailAiAnalyzer
    private lateinit var service: GmailScanService

    @Before
    fun setUp() {
        fakeApiClient = FakeGmailApiClient()
        fakeAnalyzer  = FakeGmailAiAnalyzer()
        service = GmailScanService(fakeApiClient, fakeAnalyzer)
    }

    @Test
    fun `scan restituisce ApiError quando listMessageIds fallisce`() {
        fakeApiClient.listResult = null
        val result = service.scan("token", 0L, emptySet(), "gemini-key")
        assertTrue(result is ScanResult.ApiError)
    }

    @Test
    fun `scan restituisce Done vuoto quando non ci sono email nuove`() {
        fakeApiClient.listResult = listOf("id1", "id2")
        val result = service.scan("token", 0L, setOf("id1", "id2"), "gemini-key")
        assertTrue(result is ScanResult.Done)
        assertEquals(0, (result as ScanResult.Done).newItems.size)
    }

    @Test
    fun `scan restituisce AiUnavailable senza geminiKey`() {
        fakeApiClient.listResult = listOf("id1")
        val result = service.scan("token", 0L, emptySet(), geminiKey = null)
        assertTrue(result is ScanResult.AiUnavailable)
    }

    @Test
    fun `scan inserisce item quando AI trova email interessante`() {
        fakeApiClient.listResult = listOf("id1")
        fakeApiClient.metaResult = EmailMeta("id1", "Bolletta", "eni@eni.it", "Mon", "scadenza")
        fakeApiClient.bodyResult = "testo email bolletta"
        fakeAnalyzer.phase1Result = listOf(
            AnalysisResult(
                EmailMeta("id1", "Bolletta", "eni@eni.it", "Mon", "scadenza"),
                PendingItemType.REMINDER,
                "bolletta da pagare"
            )
        )
        fakeAnalyzer.phase2Result = JsonObject().apply {
            addProperty("title", "Bolletta ENI maggio")
            addProperty("due_date", "2026-06-15")
            addProperty("amount", 87.40f)
            addProperty("is_important", true)
            addProperty("description", "Bolletta gas")
            addProperty("category", "BILL")
        }

        val result = service.scan("token", 0L, emptySet(), "gemini-key")
        assertTrue(result is ScanResult.Done)
        val done = result as ScanResult.Done
        assertEquals(1, done.newItems.size)
        assertEquals("Bolletta ENI maggio", done.newItems[0].title)
        assertEquals("id1", done.newItems[0].sourceEmailId)
        assertEquals(PendingItemType.REMINDER, done.newItems[0].itemType)
    }

    @Test
    fun `scan salta email se phase2 restituisce null`() {
        fakeApiClient.listResult = listOf("id1")
        fakeApiClient.metaResult = EmailMeta("id1", "Test", "a@b.com", "Mon", "")
        fakeApiClient.bodyResult = "body"
        fakeAnalyzer.phase1Result = listOf(
            AnalysisResult(EmailMeta("id1", "Test", "a@b.com", "Mon", ""), PendingItemType.TODO, "motivo")
        )
        fakeAnalyzer.phase2Result = null

        val result = service.scan("token", 0L, emptySet(), "gemini-key")
        assertTrue(result is ScanResult.Done)
        assertEquals(0, (result as ScanResult.Done).newItems.size)
    }

    // ─── Fake implementations ─────────────────────────────────────────────────

    class FakeGmailApiClient : GmailApiClient() {
        var listResult: List<String>? = emptyList()
        var metaResult: EmailMeta? = null
        var bodyResult: String? = null

        override fun listMessageIds(token: String, sinceEpochSec: Long) = listResult
        override fun fetchMeta(token: String, messageId: String) = metaResult
        override fun fetchBody(token: String, messageId: String) = bodyResult
    }

    class FakeGmailAiAnalyzer : GmailAiAnalyzer() {
        var phase1Result: List<AnalysisResult>? = emptyList()
        var phase2Result: JsonObject? = null

        override fun phase1Filter(emails: List<EmailMeta>, geminiKey: String?) = phase1Result
        override fun phase2Extract(email: EmailMeta, body: String, type: PendingItemType, geminiKey: String?) = phase2Result
    }
}

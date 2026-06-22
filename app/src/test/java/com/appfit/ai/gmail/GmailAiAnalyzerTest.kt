package com.appfit.ai.gmail

import com.appfit.data.model.PendingItemType
import org.junit.Assert.*
import org.junit.Assume.assumeNotNull
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import java.util.Properties

class GmailAiAnalyzerTest {

    private lateinit var analyzer: GmailAiAnalyzer
    private var geminiKey: String? = null

    @Before
    fun setUp() {
        analyzer = GmailAiAnalyzer()
        geminiKey = loadConfig("gemini_api_key")
    }

    // ─── Phase 1: filter ──────────────────────────────────────────────────────

    @Test
    fun `phase1 riconosce bolletta come REMINDER`() {
        assumeNotNull(geminiKey)
        val emails = listOf(
            EmailMeta("id1", "Bolletta gas maggio 2026", "noreply@eni.it",
                "Fri, 23 May 2026 08:00:00 +0200",
                "La tua bolletta di 87,40 EUR è disponibile. Scadenza 15 giugno.")
        )
        val results = callWithRateLimitSkip { analyzer.phase1Filter(emails, geminiKey) }
        assertNotNull("L'AI deve rispondere", results)
        assertEquals("Deve trovare 1 elemento", 1, results!!.size)
        assertEquals("Deve essere REMINDER", PendingItemType.REMINDER, results[0].type)
        assertEquals("ID corretto", "id1", results[0].emailMeta.id)
    }

    @Test
    fun `phase1 riconosce visita medica come ACTIVITY`() {
        assumeNotNull(geminiKey)
        val emails = listOf(
            EmailMeta("id2", "Conferma appuntamento dermatologo", "noreply@miodottore.it",
                "Thu, 22 May 2026 10:00:00 +0200",
                "Appuntamento confermato per lunedì 2 giugno alle 10:30 con Dr. Rossi.")
        )
        val results = callWithRateLimitSkip { analyzer.phase1Filter(emails, geminiKey) }
        assertNotNull(results)
        assertEquals(1, results!!.size)
        assertEquals(PendingItemType.ACTIVITY, results[0].type)
    }

    @Test
    fun `phase1 ignora email di marketing`() {
        assumeNotNull(geminiKey)
        val emails = listOf(
            EmailMeta("id3", "Sconto 50% solo oggi!", "promo@shop.com",
                "Fri, 23 May 2026 09:00:00 +0200",
                "Approfitta dello sconto del 50% su tutti gli articoli. Offerta valida 24h."),
            EmailMeta("id4", "Newsletter settimanale", "news@blog.it",
                "Fri, 23 May 2026 07:00:00 +0200",
                "Le ultime notizie dal mondo della tecnologia e dell'innovazione.")
        )
        val results = callWithRateLimitSkip { analyzer.phase1Filter(emails, geminiKey) }
        assertNotNull(results)
        assertTrue("Promo e newsletter non devono essere flaggate", results!!.isEmpty())
    }

    @Test
    fun `phase1 gestisce lista vuota`() {
        assumeNotNull(geminiKey)
        val results = callWithRateLimitSkip { analyzer.phase1Filter(emptyList(), geminiKey) }
        assertNotNull(results)
        assertTrue(results!!.isEmpty())
    }

    // ─── Phase 2: extract ─────────────────────────────────────────────────────

    @Test
    fun `phase2 estrae dati bolletta`() {
        assumeNotNull(geminiKey)
        val email = EmailMeta("id1", "Bolletta gas maggio 2026", "noreply@eni.it",
            "Fri, 23 May 2026 08:00:00 +0200", "")
        val body = """
            Gentile cliente,
            la sua bolletta del gas per il mese di maggio 2026 è di EUR 87,40.
            Scadenza pagamento: 15 giugno 2026.
            IBAN: IT60 X054 2811 1010 0000 0123 456
        """.trimIndent()

        val payload = callWithRateLimitSkip { analyzer.phase2Extract(email, body, PendingItemType.REMINDER, geminiKey) }
        assertNotNull("Deve estrarre il payload", payload)
        val title = payload!!.get("title")?.asString
        assertNotNull("title non deve essere null", title)
        assertTrue("title deve contenere info utili", title!!.isNotBlank())
        println("due_date estratto: ${payload.get("due_date")?.asString}")
    }

    @Test
    fun `phase2 estrae dati visita medica`() {
        assumeNotNull(geminiKey)
        val email = EmailMeta("id2", "Conferma appuntamento", "noreply@miodottore.it",
            "Thu, 22 May 2026 10:00:00 +0200", "")
        val body = """
            La sua prenotazione è confermata.
            Specialista: Dr. Rossi (Dermatologia)
            Data: lunedì 2 giugno 2026 alle ore 10:30
            Indirizzo: Via Roma 1, Milano
            Durata prevista: 30 minuti
        """.trimIndent()

        val payload = callWithRateLimitSkip { analyzer.phase2Extract(email, body, PendingItemType.ACTIVITY, geminiKey) }
        assertNotNull(payload)
        println("scheduled_date estratto: ${payload!!.get("scheduled_date")?.asString}")
        println("scheduled_time estratto: ${payload.get("scheduled_time")?.asString}")
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private fun <T> callWithRateLimitSkip(block: () -> T): T {
        return try {
            block()
        } catch (e: GeminiRateLimitException) {
            assumeTrue("Gemini rate limit (429) — quota esaurita, riprova più tardi.\n${e.message}", false)
            throw e // unreachable, ma necessario per il compilatore
        }
    }

    private fun loadConfig(key: String): String? {
        val props = Properties()
        val stream = javaClass.classLoader?.getResourceAsStream("test_config.properties")
            ?: return System.getenv(key.uppercase().replace('.', '_'))
        return try {
            props.load(stream)
            props.getProperty(key)?.takeIf { it.isNotBlank() && !it.startsWith("YOUR_") }
        } catch (e: Exception) { null }
    }
}

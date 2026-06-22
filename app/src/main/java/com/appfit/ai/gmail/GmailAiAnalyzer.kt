package com.appfit.ai.gmail

import com.appfit.data.model.PendingItemType
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

class GeminiRateLimitException(message: String) : Exception(message)

@Singleton
open class GmailAiAnalyzer @Inject constructor() {

    private val gson = Gson()

    open fun phase1Filter(emails: List<EmailMeta>, geminiKey: String?): List<AnalysisResult>? {
        val today = LocalDate.now()
        val emailsText = emails.joinToString("\n---\n") { e ->
            "ID:${e.id}\nDa: ${e.from}\nOggetto: ${e.subject}\nData: ${e.date}\nAnteprima: ${e.snippet}"
        }
        val prompt = """
Sei un assistente personale per un'app di fitness e salute. Analizza queste email e identifica SOLO quelle che contengono informazioni actionable per l'utente.
Tipi validi:
- ACTIVITY: SOLO attività fisiche/sportive (palestra, corsa, nuoto, yoga, ciclismo) e visite mediche/fisioterapia. NON usare per appuntamenti lavorativi, colloqui, riunioni o eventi sociali.
- REMINDER: scadenze, bollette, pagamenti, rinnovi, assicurazioni, bolli, appuntamenti lavorativi (colloqui, riunioni, interviste), eventi con data e ora che non sono sport
- TODO: cose da fare, documenti da portare, azioni richieste entro una data

Oggi è $today.
Rispondi SOLO con JSON (nessun testo prima o dopo):
{"interesting":[{"id":"gmail_msg_id","type":"ACTIVITY|REMINDER|TODO","reason":"breve motivo in italiano"}]}
Se nessuna email è interessante: {"interesting":[]}

EMAIL:
$emailsText
        """.trimIndent()

        println("[GmailAiAnalyzer] phase1: ${emails.size} email, prompt=${prompt.length} chars")
        val response = callGemini(geminiKey ?: return null, prompt) ?: return null
        println("[GmailAiAnalyzer] phase1 response: ${response.take(200)}")

        return try {
            val jsonStart = response.indexOf('{')
            val jsonEnd   = response.lastIndexOf('}')
            if (jsonStart < 0 || jsonEnd < 0) return emptyList()
            val root  = JsonParser.parseString(response.substring(jsonStart, jsonEnd + 1)).asJsonObject
            val array = root.getAsJsonArray("interesting") ?: return emptyList()
            val emailById = emails.associateBy { it.id }
            array.mapNotNull { el ->
                val obj    = el.asJsonObject
                val id     = obj.get("id")?.asString ?: return@mapNotNull null
                val typeStr= obj.get("type")?.asString ?: return@mapNotNull null
                val reason = obj.get("reason")?.asString ?: ""
                val email  = emailById[id] ?: return@mapNotNull null
                val type   = runCatching { PendingItemType.valueOf(typeStr) }.getOrNull() ?: return@mapNotNull null
                AnalysisResult(email, type, reason)
            }
        } catch (e: Exception) {
            println("[GmailAiAnalyzer] phase1 parse error: ${e.message}")
            emptyList()
        }
    }

    open fun phase2Extract(email: EmailMeta, body: String, type: PendingItemType, geminiKey: String?): JsonObject? {
        val today = LocalDate.now()
        val schema = when (type) {
            PendingItemType.ACTIVITY ->
                """{"title":"...","type":"CARDIO|STRENGTH|FLEXIBILITY|YOGA|REST|CUSTOM","scheduled_date":"YYYY-MM-DD","scheduled_time":"HH:mm o null","duration_minutes":60,"description":"...","calories_burned":0}"""
            PendingItemType.REMINDER ->
                """{"title":"...","category":"BILL|CAR|DOCUMENT|HEALTH|SUBSCRIPTION|OTHER","due_date":"YYYY-MM-DD o null","amount":null,"is_important":true,"description":"..."}"""
            PendingItemType.TODO ->
                """{"title":"...","due_date":"YYYY-MM-DD o null","is_important":false,"description":"..."}"""
        }
        val prompt = """
Analizza questa email e estrai le informazioni strutturate. Tipo: ${type.name}. Oggi è $today.
Rispondi SOLO con JSON seguendo esattamente questo schema (nessun testo prima o dopo):
$schema

Da: ${email.from}
Oggetto: ${email.subject}
Data: ${email.date}
---
$body
        """.trimIndent()

        println("[GmailAiAnalyzer] phase2: '${email.subject}', prompt=${prompt.length} chars")
        val response = callGemini(geminiKey ?: return null, prompt) ?: return null
        println("[GmailAiAnalyzer] phase2 response: ${response.take(200)}")

        return try {
            val jsonStart = response.indexOf('{')
            val jsonEnd   = response.lastIndexOf('}')
            if (jsonStart < 0 || jsonEnd < 0) return null
            JsonParser.parseString(response.substring(jsonStart, jsonEnd + 1)).asJsonObject
        } catch (e: Exception) {
            println("[GmailAiAnalyzer] phase2 parse error: ${e.message}")
            null
        }
    }

    fun callGemini(apiKey: String, prompt: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val body = JsonObject().apply {
            add("contents", JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("role", "user")
                    add("parts", JsonArray().apply {
                        add(JsonObject().apply { addProperty("text", prompt) })
                    })
                })
            })
        }
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 30_000
            conn.readTimeout    = 60_000
            OutputStreamWriter(conn.outputStream).use { it.write(gson.toJson(body)) }
            val code = conn.responseCode
            if (code == 200) {
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                JsonParser.parseString(text).asJsonObject
                    .getAsJsonArray("candidates")
                    ?.get(0)?.asJsonObject
                    ?.getAsJsonObject("content")
                    ?.getAsJsonArray("parts")
                    ?.get(0)?.asJsonObject
                    ?.get("text")?.asString
            } else {
                val err = conn.errorStream?.bufferedReader()?.readText() ?: "(no body)"
                conn.disconnect()
                if (code == 429) throw GeminiRateLimitException(err)
                println("[GmailAiAnalyzer] callGemini HTTP $code: $err")
                null
            }
        } catch (e: GeminiRateLimitException) {
            throw e
        } catch (e: Exception) {
            println("[GmailAiAnalyzer] callGemini exception: ${e::class.simpleName}: ${e.message}")
            null
        }
    }
}

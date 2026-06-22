package com.appfit.ai.gmail

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class GmailApiClient @Inject constructor() {

    open fun listMessageIds(token: String, sinceEpochSec: Long): List<String>? {
        val url = "https://gmail.googleapis.com/gmail/v1/users/me/messages" +
                "?maxResults=50&q=after:$sinceEpochSec"
        println("[GmailApiClient] GET $url")
        val response = httpGet(url, token) ?: return null
        val messages = response.getAsJsonArray("messages") ?: return emptyList()
        return messages.mapNotNull { it.asJsonObject.get("id")?.asString }
    }

    open fun fetchMeta(token: String, messageId: String): EmailMeta? {
        val url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/$messageId" +
                "?format=metadata&metadataHeaders=Subject&metadataHeaders=From&metadataHeaders=Date"
        val response = httpGet(url, token) ?: return null
        val headers = response.getAsJsonObject("payload")?.getAsJsonArray("headers") ?: return null
        var subject = ""; var from = ""; var date = ""
        headers.forEach { h ->
            val obj = h.asJsonObject
            when (obj.get("name")?.asString) {
                "Subject" -> subject = obj.get("value")?.asString ?: ""
                "From"    -> from    = obj.get("value")?.asString ?: ""
                "Date"    -> date    = obj.get("value")?.asString ?: ""
            }
        }
        val snippet = response.get("snippet")?.asString ?: ""
        return EmailMeta(messageId, subject, from, date, snippet)
    }

    open fun fetchBody(token: String, messageId: String): String? {
        val url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/$messageId?format=full"
        val response = httpGet(url, token) ?: return null
        val payload = response.getAsJsonObject("payload") ?: return null
        return extractText(payload)?.take(4000)
    }

    fun httpGet(url: String, token: String): JsonObject? = try {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.connectTimeout = 15_000
        conn.readTimeout    = 15_000
        val code = conn.responseCode
        if (code == 200) {
            val body = conn.inputStream.bufferedReader().readText()
            conn.disconnect()
            JsonParser.parseString(body).asJsonObject
        } else {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "(no body)"
            println("[GmailApiClient] HTTP $code — ${url.substringBefore('?')}: $err")
            conn.disconnect()
            null
        }
    } catch (e: Exception) {
        println("[GmailApiClient] exception: ${e::class.simpleName}: ${e.message}")
        null
    }

    private fun extractText(payload: JsonObject): String? {
        val mimeType = payload.get("mimeType")?.asString ?: ""
        if (mimeType == "text/plain") {
            val data = payload.getAsJsonObject("body")?.get("data")?.asString ?: return null
            return decodeBase64Url(data)
        }
        val parts = payload.getAsJsonArray("parts") ?: return null
        for (preferred in listOf("text/plain", "text/html")) {
            parts.forEach { el ->
                val part = el.asJsonObject
                if (part.get("mimeType")?.asString == preferred) {
                    val data = part.getAsJsonObject("body")?.get("data")?.asString
                    if (data != null) return decodeBase64Url(data)
                }
            }
        }
        parts.forEach { el ->
            val result = extractText(el.asJsonObject)
            if (result != null) return result
        }
        return null
    }

    fun decodeBase64Url(encoded: String): String = try {
        String(Base64.getUrlDecoder().decode(encoded))
    } catch (e: Exception) {
        try {
            // Some Gmail messages use standard base64 with padding stripped
            val padded = encoded + "=".repeat((4 - encoded.length % 4) % 4)
            String(Base64.getDecoder().decode(padded.replace('-', '+').replace('_', '/')))
        } catch (e2: Exception) { "" }
    }
}

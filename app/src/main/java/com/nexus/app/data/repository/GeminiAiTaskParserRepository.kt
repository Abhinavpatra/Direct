package com.nexus.app.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.data.remote.ParsedTaskPayload
import com.nexus.app.domain.repository.AiTaskParserRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GeminiAiTaskParserRepository @Inject constructor(
    private val json: Json,
    private val timeProvider: TimeProvider,
    private val userPreferencesRepository: UserPreferencesRepository,
) : AiTaskParserRepository {
    override suspend fun parse(text: String, currentTime: String?): AppResult<ParsedTaskPayload> {
        val apiKey = userPreferencesRepository.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            return AppResult.Error(ERR_NO_API_KEY)
        }
        return try {
            val model = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
            )
            val timeToUse = currentTime ?: DateTimeFormatter.ISO_INSTANT.format(timeProvider.now())
            val prompt = "$SYSTEM_PROMPT\n\nCurrent Time (UTC/Local): $timeToUse\nUser input:\n$text"
            val response = model.generateContent(prompt)
            val raw = response.text?.trim().orEmpty()
            if (raw.isBlank()) {
                AppResult.Error(ERR_AI_EMPTY)
            } else {
                // Strip markdown code fences if present
                val cleaned = raw
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()
                try {
                    AppResult.Success(json.decodeFromString<ParsedTaskPayload>(cleaned))
                } catch (e: SerializationException) {
                    AppResult.Error(ERR_AI_INVALID_JSON)
                }
            }
        } catch (error: SerializationException) {
            AppResult.Error(ERR_AI_INVALID_JSON)
        } catch (error: Exception) {
            val message = error.message ?: ""
            AppResult.Error(
                when {
                    message.contains("API key", ignoreCase = true) -> ERR_INVALID_API_KEY
                    message.contains("network", ignoreCase = true) -> ERR_AI_NETWORK
                    message.contains("unavailable", ignoreCase = true) -> ERR_AI_UNAVAILABLE
                    else -> ERR_AI_GENERIC
                }
            )
        }
    }

    private companion object {
        const val MODEL_NAME = "gemini-2.0-flash"
        const val ERR_NO_API_KEY = "No Gemini API key set. Add your key in Settings or during onboarding."
        const val ERR_INVALID_API_KEY = "Invalid API key. Check your Gemini API key in Settings."
        const val ERR_AI_EMPTY = "AI took too long to respond. Try again or simplify your input."
        const val ERR_AI_INVALID_JSON = "AI returned invalid data. Please try again with a clearer task."
        const val ERR_AI_UNAVAILABLE = "AI service unavailable. Please try again later."
        const val ERR_AI_NETWORK = "No internet connection. AI features need internet."
        const val ERR_AI_GENERIC = "AI failed. Please try again."
        const val SYSTEM_PROMPT = """
            You are a strict personal assistant. Return only valid JSON with these fields:
            {
              "task": string,
              "time": string,
              "reason": string,
              "actionType": "reminder" | "call" | "meeting" | "other"
            }

            Rules:
            - If reason is missing or unclear, ask the user why the task matters.
            - Do not invent a reason.
            - Time must be a valid future ISO-8601 timestamp (e.g. 2026-04-20T17:00:00Z).
            - Always return JSON only when all required fields are clear, no extra text.
        """
    }
}

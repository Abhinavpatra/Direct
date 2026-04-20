package com.nexus.app.data.repository

import com.google.firebase.ai.FirebaseAI
import com.nexus.app.core.result.AppResult
import com.nexus.app.data.remote.ParsedTaskPayload
import com.nexus.app.domain.repository.AiTaskParserRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FirebaseAiTaskParserRepository @Inject constructor(
    private val json: Json,
) : AiTaskParserRepository {
    override suspend fun parse(text: String): AppResult<ParsedTaskPayload> = try {
        val model = FirebaseAI.instance.generativeModel(
            modelName = MODEL_NAME,
        )
        val prompt = "$SYSTEM_PROMPT\n\nUser input:\n$text"
        val response = model.generateContent(prompt)
        val raw = response.text?.trim().orEmpty()
        if (raw.isBlank()) {
            AppResult.Error("AI response was empty")
        } else {
            AppResult.Success(json.decodeFromString<ParsedTaskPayload>(raw))
        }
    } catch (error: SerializationException) {
        AppResult.Error("AI response was not valid JSON", error)
    } catch (error: Exception) {
        AppResult.Error("Failed to parse task with Firebase AI Logic", error)
    }

    private companion object {
        const val MODEL_NAME = "gemini-2.5-flash"
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
            - Time must be a valid future ISO-8601 timestamp.
            - Return JSON only when all required fields are clear.
        """
    }
}

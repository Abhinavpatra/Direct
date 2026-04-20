package com.nexus.app.data.repository

import com.nexus.app.core.result.AppResult
import com.nexus.app.data.remote.ParsedTaskPayload
import com.nexus.app.domain.repository.AiTaskParserRepository
import javax.inject.Inject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Fallback parser for offline use. Uses basic pattern matching 
 * to extract tasks and times when AI is unavailable.
 */
class LocalAiTaskParserRepository @Inject constructor() : AiTaskParserRepository {
    override suspend fun parse(text: String): AppResult<ParsedTaskPayload> {
        return try {
            val lowercaseText = text.lowercase()
            
            // Basic extraction logic for offline mode
            val task = text.take(50).trim()
            val time = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME)
            val reason = if (lowercaseText.contains("because")) {
                text.substringAfter("because").trim()
            } else {
                "" // Will trigger the "ask for reason" flow in the UI if needed
            }

            if (reason.isEmpty()) {
                AppResult.Error("Offline mode: Please provide a reason (e.g., '... because [reason]')")
            } else {
                AppResult.Success(
                    ParsedTaskPayload(
                        task = task,
                        time = time,
                        reason = reason,
                        actionType = "reminder"
                    )
                )
            }
        } catch (e: Exception) {
            AppResult.Error("Offline parsing failed", e)
        }
    }
}

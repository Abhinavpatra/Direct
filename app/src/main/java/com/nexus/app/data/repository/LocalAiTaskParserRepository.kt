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
    override suspend fun parse(text: String, currentTime: String?): AppResult<ParsedTaskPayload> {
        return try {
            val lowercaseText = text.lowercase()

            // Basic extraction logic for offline mode
            val task = text.take(50).trim()
            val time = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME)
            val reason = if (lowercaseText.contains("because")) {
                text.substringAfter("because").trim()
            } else {
                ""
            }

            if (reason.isEmpty()) {
                AppResult.Error(ERR_OFFLINE_NO_REASON)
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
            AppResult.Error(ERR_OFFLINE_PARSE_FAILED)
        }
    }

    private companion object {
        const val ERR_OFFLINE_NO_REASON = "Please add why this task matters (e.g., 'because meeting with boss')."
        const val ERR_OFFLINE_PARSE_FAILED = "Could not understand input. Try: 'Remind me to [task] at [time] because [reason]'."
    }
}

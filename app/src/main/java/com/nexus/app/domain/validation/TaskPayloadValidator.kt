package com.nexus.app.domain.validation

import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.IsoDateParser
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.data.remote.ParsedTaskPayload
import com.nexus.app.domain.model.ActionType
import java.time.Instant
import javax.inject.Inject

class TaskPayloadValidator @Inject constructor(
    private val isoDateParser: IsoDateParser,
    private val timeProvider: TimeProvider,
) {
    fun validate(payload: ParsedTaskPayload): AppResult<ValidatedTaskPayload> {
        if (payload.task.isBlank()) return AppResult.Error("Task title is required")
        if (payload.reason.isBlank()) return AppResult.Error("Reason is required")
        val actionType = ActionType.fromRaw(payload.actionType)
            ?: return AppResult.Error("Action type must be reminder, call, meeting, or other")
        val instant = when (val parsed = isoDateParser.parse(payload.time)) {
            is AppResult.Error -> return parsed
            is AppResult.Success -> parsed.value
        }
        if (!instant.isAfter(timeProvider.now())) {
            return AppResult.Error("Time must be in the future")
        }
        return AppResult.Success(
            ValidatedTaskPayload(
                task = payload.task.trim(),
                scheduledAt = instant,
                reason = payload.reason.trim(),
                actionType = actionType,
            ),
        )
    }
}

data class ValidatedTaskPayload(
    val task: String,
    val scheduledAt: Instant,
    val reason: String,
    val actionType: ActionType,
)

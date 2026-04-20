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
        if (payload.task.isBlank()) return AppResult.Error(ERR_TASK_BLANK)
        if (payload.reason.isBlank()) return AppResult.Error(ERR_REASON_BLANK)
        val actionType = ActionType.fromRaw(payload.actionType)
            ?: return AppResult.Error(ERR_ACTION_TYPE)
        val instant = when (val parsed = isoDateParser.parse(payload.time)) {
            is AppResult.Error -> return AppResult.Error(ERR_TIME_FORMAT)
            is AppResult.Success -> parsed.value
        }
        if (!instant.isAfter(timeProvider.now())) {
            return AppResult.Error(ERR_TIME_PAST)
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

    private companion object {
        const val ERR_TASK_BLANK = "What do you want to be reminded about?"
        const val ERR_REASON_BLANK = "Why is this important?"
        const val ERR_ACTION_TYPE = "Please specify: reminder, call, meeting, or other"
        const val ERR_TIME_FORMAT = "AI failed to generate a valid time format. Please try again or specify clearly."
        const val ERR_TIME_PAST = "Please set a future time for the reminder."
    }
}

data class ValidatedTaskPayload(
    val task: String,
    val scheduledAt: Instant,
    val reason: String,
    val actionType: ActionType,
)

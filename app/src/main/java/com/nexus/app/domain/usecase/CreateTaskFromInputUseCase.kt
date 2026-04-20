package com.nexus.app.domain.usecase

import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.domain.model.Task
import com.nexus.app.domain.model.TaskStatus
import com.nexus.app.domain.repository.AiTaskParserRepository
import com.nexus.app.domain.repository.TaskRepository
import com.nexus.app.domain.validation.TaskPayloadValidator
import com.nexus.app.reminder.alarm.ReminderScheduler
import javax.inject.Inject

class CreateTaskFromInputUseCase @Inject constructor(
    private val aiTaskParserRepository: AiTaskParserRepository,
    private val taskPayloadValidator: TaskPayloadValidator,
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(text: String): AppResult<Task> {
        val parsed = aiTaskParserRepository.parse(text)
        val payload = when (parsed) {
            is AppResult.Error -> return parsed
            is AppResult.Success -> parsed.value
        }
        val validated = when (val result = taskPayloadValidator.validate(payload)) {
            is AppResult.Error -> return result
            is AppResult.Success -> result.value
        }
        if (!reminderScheduler.canScheduleExactAlarms()) {
            return AppResult.Error("Exact alarm access is not enabled")
        }
        val now = timeProvider.now().toEpochMilli()
        val draft = Task(
            id = 0,
            title = validated.task,
            scheduledAtEpochMillis = validated.scheduledAt.toEpochMilli(),
            reason = validated.reason,
            actionType = validated.actionType,
            status = TaskStatus.PENDING,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
        )
        val savedId = taskRepository.upsert(draft)
        val savedTask = draft.copy(id = savedId)
        reminderScheduler.schedule(
            taskId = savedId,
            title = savedTask.title,
            reason = savedTask.reason,
            triggerAtMillis = savedTask.scheduledAtEpochMillis,
        )
        return AppResult.Success(savedTask)
    }
}

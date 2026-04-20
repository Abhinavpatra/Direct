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
        var currentText = text
        var retryCount = 0
        var lastError: AppResult.Error? = null
        val currentTime = timeProvider.now().toString()

        while (retryCount < 2) {
            val parsedResult = aiTaskParserRepository.parse(currentText, currentTime)
            if (parsedResult is AppResult.Error) {
                lastError = parsedResult
                retryCount++
                currentText = "$text\nYour previous response was invalid: ${parsedResult.message}. Please generate valid JSON and ISO-8601 time."
                continue
            }

            val payload = (parsedResult as AppResult.Success).value
            val validatedResult = taskPayloadValidator.validate(payload)
            if (validatedResult is AppResult.Error) {
                lastError = validatedResult
                retryCount++
                currentText = "$text\nYour previous response was invalid: ${validatedResult.message}. Please fix it."
                continue
            }

            val validated = (validatedResult as AppResult.Success).value

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
        return lastError ?: AppResult.Error("Failed to parse task after multiple attempts.")
    }
}

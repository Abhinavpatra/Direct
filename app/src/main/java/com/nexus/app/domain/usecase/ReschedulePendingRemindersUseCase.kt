package com.nexus.app.domain.usecase

import com.nexus.app.domain.repository.TaskRepository
import com.nexus.app.reminder.alarm.ReminderScheduler
import javax.inject.Inject

class ReschedulePendingRemindersUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke() {
        taskRepository.getPendingTasks().forEach { task ->
            reminderScheduler.schedule(
                taskId = task.id,
                title = task.title,
                reason = task.reason,
                triggerAtMillis = task.scheduledAtEpochMillis,
            )
        }
    }
}

package com.nexus.app.domain.usecase

import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.domain.model.TaskStatus
import com.nexus.app.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(taskId: Long, status: TaskStatus): AppResult<Unit> {
        val existing = taskRepository.getTask(taskId) ?: return AppResult.Error("Task not found")
        taskRepository.upsert(
            existing.copy(
                status = status,
                updatedAtEpochMillis = timeProvider.now().toEpochMilli(),
            ),
        )
        return AppResult.Success(Unit)
    }
}

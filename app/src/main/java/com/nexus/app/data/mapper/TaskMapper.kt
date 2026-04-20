package com.nexus.app.data.mapper

import com.nexus.app.data.local.entity.TaskEntity
import com.nexus.app.domain.model.ActionType
import com.nexus.app.domain.model.Task
import com.nexus.app.domain.model.TaskStatus
import javax.inject.Inject

class TaskMapper @Inject constructor() {
    fun toDomain(entity: TaskEntity): Task = Task(
        id = entity.id,
        title = entity.title,
        scheduledAtEpochMillis = entity.scheduledAtEpochMillis,
        reason = entity.reason,
        actionType = ActionType.fromRaw(entity.actionType) ?: ActionType.OTHER,
        status = TaskStatus.valueOf(entity.status),
        createdAtEpochMillis = entity.createdAtEpochMillis,
        updatedAtEpochMillis = entity.updatedAtEpochMillis,
    )

    fun toEntity(domain: Task): TaskEntity = TaskEntity(
        id = domain.id,
        title = domain.title,
        scheduledAtEpochMillis = domain.scheduledAtEpochMillis,
        reason = domain.reason,
        actionType = domain.actionType.name.lowercase(),
        status = domain.status.name,
        createdAtEpochMillis = domain.createdAtEpochMillis,
        updatedAtEpochMillis = domain.updatedAtEpochMillis,
    )
}

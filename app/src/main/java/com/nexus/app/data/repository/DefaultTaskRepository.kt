package com.nexus.app.data.repository

import com.nexus.app.data.local.dao.TaskDao
import com.nexus.app.data.mapper.TaskMapper
import com.nexus.app.domain.model.Task
import com.nexus.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper,
) : TaskRepository {
    override fun observeTasks(): Flow<List<Task>> =
        taskDao.observeAll().map { entities -> entities.map(taskMapper::toDomain) }

    override suspend fun upsert(task: Task): Long {
        val id = taskDao.insert(taskMapper.toEntity(task))
        if (task.id != 0L) return task.id
        return id
    }

    override suspend fun getPendingTasks(): List<Task> =
        taskDao.getByStatus("PENDING").map(taskMapper::toDomain)

    override suspend fun getTask(id: Long): Task? =
        taskDao.getById(id)?.let(taskMapper::toDomain)
}

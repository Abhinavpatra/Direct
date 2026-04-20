package com.nexus.app.domain.repository

import com.nexus.app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun upsert(task: Task): Long
    suspend fun getPendingTasks(): List<Task>
    suspend fun getTask(id: Long): Task?
}

package com.nexus.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.data.remote.ParsedTaskPayload
import com.nexus.app.domain.model.Task
import com.nexus.app.domain.repository.AiTaskParserRepository
import com.nexus.app.domain.repository.TaskRepository
import com.nexus.app.domain.validation.TaskPayloadValidator
import com.nexus.app.reminder.alarm.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class CreateTaskFromInputUseCaseTest {
    private val fixedNow = Instant.parse("2026-04-19T10:00:00Z")
    private val validator = TaskPayloadValidator(
        isoDateParser = com.nexus.app.core.time.IsoDateParser(),
        timeProvider = object : TimeProvider {
            override fun now(): Instant = fixedNow
        },
    )

    @Test
    fun `returns error when exact alarms unavailable`() = runTest {
        val useCase = CreateTaskFromInputUseCase(
            aiTaskParserRepository = FakeAiRepository,
            taskPayloadValidator = validator,
            taskRepository = FakeTaskRepository(),
            reminderScheduler = object : ReminderScheduler {
                override fun canScheduleExactAlarms(): Boolean = false
                override fun schedule(taskId: Long, title: String, reason: String, triggerAtMillis: Long) = Unit
            },
            timeProvider = object : TimeProvider {
                override fun now(): Instant = fixedNow
            },
        )

        val result = useCase("Call mom tomorrow because promise")

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        assertThat((result as AppResult.Error).message).contains("Exact alarm")
    }

    private object FakeAiRepository : AiTaskParserRepository {
        override suspend fun parse(text: String): AppResult<ParsedTaskPayload> = AppResult.Success(
            ParsedTaskPayload(
                task = "Call mom",
                time = "2026-04-20T10:00:00Z",
                reason = "Promise kept",
                actionType = "call",
            ),
        )
    }

    private class FakeTaskRepository : TaskRepository {
        override fun observeTasks(): Flow<List<Task>> = flowOf(emptyList())
        override suspend fun upsert(task: Task): Long = 42
        override suspend fun getPendingTasks(): List<Task> = emptyList()
        override suspend fun getTask(id: Long): Task? = null
    }
}

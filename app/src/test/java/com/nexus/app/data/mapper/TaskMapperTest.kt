package com.nexus.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.nexus.app.data.local.entity.TaskEntity
import com.nexus.app.domain.model.ActionType
import com.nexus.app.domain.model.TaskStatus
import org.junit.Test

class TaskMapperTest {
    private val mapper = TaskMapper()

    @Test
    fun `maps entity to domain`() {
        val entity = TaskEntity(
            id = 4,
            title = "Call dentist",
            scheduledAtEpochMillis = 1234,
            reason = "Annual checkup",
            actionType = "call",
            status = "PENDING",
            createdAtEpochMillis = 1000,
            updatedAtEpochMillis = 1100,
        )

        val domain = mapper.toDomain(entity)

        assertThat(domain.id).isEqualTo(4)
        assertThat(domain.actionType).isEqualTo(ActionType.CALL)
        assertThat(domain.status).isEqualTo(TaskStatus.PENDING)
    }
}

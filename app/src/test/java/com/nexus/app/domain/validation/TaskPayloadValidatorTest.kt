package com.nexus.app.domain.validation

import com.google.common.truth.Truth.assertThat
import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.IsoDateParser
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.data.remote.ParsedTaskPayload
import org.junit.Test
import java.time.Instant

class TaskPayloadValidatorTest {
    private val fixedNow = Instant.parse("2026-04-19T10:00:00Z")
    private val validator = TaskPayloadValidator(
        isoDateParser = IsoDateParser(),
        timeProvider = object : TimeProvider {
            override fun now(): Instant = fixedNow
        },
    )

    @Test
    fun `returns error when reason missing`() {
        val result = validator.validate(
            ParsedTaskPayload(
                task = "Call mom",
                time = "2026-04-19T12:00:00Z",
                reason = "",
                actionType = "call",
            ),
        )

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        assertThat((result as AppResult.Error).message).contains("Reason")
    }

    @Test
    fun `returns error when time is in past`() {
        val result = validator.validate(
            ParsedTaskPayload(
                task = "Pay rent",
                time = "2026-04-18T12:00:00Z",
                reason = "Avoid late fee",
                actionType = "reminder",
            ),
        )

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        assertThat((result as AppResult.Error).message).contains("future")
    }

    @Test
    fun `returns success for valid payload`() {
        val result = validator.validate(
            ParsedTaskPayload(
                task = "Prepare slides",
                time = "2026-04-20T12:00:00Z",
                reason = "Leadership review",
                actionType = "meeting",
            ),
        )

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val value = (result as AppResult.Success).value
        assertThat(value.task).isEqualTo("Prepare slides")
    }
}

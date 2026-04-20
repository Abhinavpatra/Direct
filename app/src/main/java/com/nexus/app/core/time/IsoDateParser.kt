package com.nexus.app.core.time

import com.nexus.app.core.result.AppResult
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsoDateParser @Inject constructor() {
    fun parse(value: String): AppResult<Instant> = try {
        AppResult.Success(Instant.parse(value))
    } catch (error: DateTimeParseException) {
        AppResult.Error(ERR_TIME_INVALID)
    }

    private companion object {
        const val ERR_TIME_INVALID = "Invalid time format. Use format like '2026-04-20T15:00:00Z' or try 'today 3pm'."
    }
}

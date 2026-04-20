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
        AppResult.Error("Time must be valid ISO-8601", error)
    }
}

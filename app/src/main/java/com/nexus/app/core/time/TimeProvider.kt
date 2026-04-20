package com.nexus.app.core.time

import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

interface TimeProvider {
    fun now(): Instant
}

@Singleton
class DefaultTimeProvider @Inject constructor() : TimeProvider {
    private val clock: Clock = Clock.systemDefaultZone()

    override fun now(): Instant = Instant.now(clock)
}

package com.nexus.app.domain.repository

import com.nexus.app.domain.model.CalendarEvent

interface CalendarRepository {
    suspend fun readUpcomingEvents(limit: Int = 50): List<CalendarEvent>
}

package com.nexus.app.domain.repository

import com.nexus.app.domain.model.CalendarEvent

interface CalendarRepository {
    suspend fun readEvents(startEpochMillis: Long, endEpochMillis: Long): List<CalendarEvent>
    suspend fun insertEvent(title: String, startEpochMillis: Long, endEpochMillis: Long, location: String?): Long
    suspend fun updateEvent(id: Long, title: String, startEpochMillis: Long, endEpochMillis: Long, location: String?)
    suspend fun deleteEvent(id: Long)
}

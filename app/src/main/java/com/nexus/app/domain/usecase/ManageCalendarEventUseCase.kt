package com.nexus.app.domain.usecase

import com.nexus.app.domain.repository.CalendarRepository
import javax.inject.Inject

class ManageCalendarEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
) {
    suspend fun create(title: String, startEpochMillis: Long, endEpochMillis: Long, location: String?): Long {
        return calendarRepository.insertEvent(title, startEpochMillis, endEpochMillis, location)
    }

    suspend fun update(id: Long, title: String, startEpochMillis: Long, endEpochMillis: Long, location: String?) {
        calendarRepository.updateEvent(id, title, startEpochMillis, endEpochMillis, location)
    }

    suspend fun delete(id: Long) {
        calendarRepository.deleteEvent(id)
    }
}

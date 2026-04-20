package com.nexus.app.domain.model

import java.time.LocalDate

data class AgendaDay(
    val date: LocalDate,
    val tasks: List<Task>,
    val calendarEvents: List<CalendarEvent>,
)

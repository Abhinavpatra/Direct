package com.nexus.app.domain.model

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val location: String?,
)

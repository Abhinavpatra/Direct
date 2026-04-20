package com.nexus.app.ui.state

data class SettingsUiState(
    val notificationRationale: String,
    val calendarRationale: String,
    val exactAlarmRationale: String,
    val message: String? = null,
)

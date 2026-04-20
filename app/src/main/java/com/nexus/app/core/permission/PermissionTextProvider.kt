package com.nexus.app.core.permission

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionTextProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun notificationRationale(): String =
        "Notifications let Nexus deliver reminders at exact times."

    fun calendarRationale(): String =
        "Calendar access lets Agenda blend your device events with local tasks."

    fun exactAlarmRationale(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            "Exact alarm access is required for precise reminders while device is idle."
        } else {
            "This Android version allows exact alarms without extra user approval."
        }
}

package com.nexus.app.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nexus.app.reminder.notification.ReminderNotifier
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notifier: ReminderNotifier

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE).orEmpty()
        val reason = intent.getStringExtra(EXTRA_TASK_REASON).orEmpty()
        if (taskId <= 0 || title.isBlank()) return
        notifier.show(taskId = taskId, title = title, reason = reason)
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_TASK_REASON = "task_reason"
    }
}

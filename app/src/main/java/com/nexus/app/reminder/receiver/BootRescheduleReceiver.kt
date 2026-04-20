package com.nexus.app.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nexus.app.domain.usecase.ReschedulePendingRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootRescheduleReceiver : BroadcastReceiver() {
    @Inject
    lateinit var reschedulePendingRemindersUseCase: ReschedulePendingRemindersUseCase

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            reschedulePendingRemindersUseCase()
        }
    }
}

package com.nexus.app.ui.screen.agenda

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.app.domain.model.AgendaDay
import com.nexus.app.ui.component.SectionCard
import java.time.format.DateTimeFormatter

@Composable
fun AgendaRoute(
    viewModel: AgendaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val hasCalendarPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CALENDAR,
    ) == PackageManager.PERMISSION_GRANTED
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Agenda dashboard", style = MaterialTheme.typography.headlineLarge)
        }
        if (!hasCalendarPermission) {
            item {
                SectionCard(title = "Calendar access") {
                    Text("Grant calendar permission to blend events into agenda.")
                }
            }
        }
        items(uiState.days, key = { it.date.toEpochDay() }) { day ->
            AgendaDayCard(day = day, onMarkDone = viewModel::markDone)
        }
    }
}

@Composable
private fun AgendaDayCard(
    day: AgendaDay,
    onMarkDone: (Long) -> Unit,
) {
    SectionCard(title = day.date.format(DateTimeFormatter.ofPattern("EEE, MMM d"))) {
        if (day.tasks.isEmpty() && day.calendarEvents.isEmpty()) {
            Text("No entries")
        }
        day.tasks.forEach { task ->
            Text("Task: ${task.title}")
            Text(
                text = "${task.reason} • ${task.actionType.name.lowercase()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            if (task.status.name != "DONE") {
                OutlinedButton(
                    onClick = { onMarkDone(task.id) },
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    Text("Mark done")
                }
            }
        }
        day.calendarEvents.forEach { event ->
            Text("Calendar: ${event.title}")
            if (!event.location.isNullOrBlank()) {
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
    }
}

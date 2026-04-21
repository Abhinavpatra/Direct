package com.nexus.app.ui.screen.agenda

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.graphics.Color
import com.nexus.app.domain.model.AgendaDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.nexus.app.domain.model.CalendarEvent

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
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var eventToEdit by remember { mutableStateOf<CalendarEvent?>(null) }
    var showEventDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.days) {
        if (selectedDate == null && uiState.days.isNotEmpty()) {
            selectedDate = uiState.days.first().date
        }
    }

    Scaffold(
        floatingActionButton = {
            if (hasCalendarPermission) {
                FloatingActionButton(
                    onClick = {
                        eventToEdit = null
                        showEventDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Text(
            text = "Agenda",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 8.dp)
        )
        
        if (!hasCalendarPermission) {
            Text(
                text = "Missing calendar permission -> missing events.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // We use a large range to simulate infinite scroll
        val daysRange = -365L * 2..365L * 2
        val initialIndex = daysRange.indexOf(0L)
        val today = LocalDate.now()
        val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState(initialFirstVisibleItemIndex = if (initialIndex > -1) initialIndex - 3 else 0)

        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            items(daysRange.toList(), key = { it }) { offset ->
                val date = today.plusDays(offset)
                val isSelected = date == selectedDate
                DayTab(
                    date = date,
                    isSelected = isSelected,
                    onClick = { selectedDate = date }
                )
            }
        }

        val selectedDayData = uiState.days.find { it.date == selectedDate }
        if (selectedDayData != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedDayData.tasks.isEmpty() && selectedDayData.calendarEvents.isEmpty()) {
                    item { Text("No entries for this day.", modifier = Modifier.padding(top = 16.dp)) }
                }
                
                items(selectedDayData.tasks, key = { "t_${it.id}" }) { task ->
                    TimelineCard(
                        title = task.title,
                        subtitle = "${task.reason} • ${task.actionType.name.lowercase()}",
                        timeLabel = "Task",
                        isDone = (task.status.name == "DONE"),
                        onMarkDone = { viewModel.markDone(task.id) }
                    )
                }
                
                items(selectedDayData.calendarEvents, key = { "c_${it.id}" }) { event ->
                    TimelineCard(
                        title = event.title,
                        subtitle = event.location ?: "No location",
                        timeLabel = "Calendar",
                        isDone = true,
                        onMarkDone = {},
                        onClick = {
                            eventToEdit = event
                            showEventDialog = true
                        }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (uiState.isLoading) "Loading..." else "Select a day or sync tasks.")
            }
        }
    }
    }

    if (showEventDialog) {
        EventDialog(
            event = eventToEdit,
            selectedDate = selectedDate ?: LocalDate.now(),
            onDismiss = {
                showEventDialog = false
                eventToEdit = null
            },
            onSave = { title, startEpochMillis, endEpochMillis, location ->
                if (eventToEdit == null) {
                    viewModel.createCalendarEvent(title, startEpochMillis, endEpochMillis, location)
                } else {
                    viewModel.updateCalendarEvent(eventToEdit!!.id, title, startEpochMillis, endEpochMillis, location)
                }
                showEventDialog = false
                eventToEdit = null
            },
            onDelete = { id ->
                viewModel.deleteCalendarEvent(id)
                showEventDialog = false
                eventToEdit = null
            }
        )
    }
}

@Composable
private fun DayTab(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEE")).uppercase()
    val dayOfMonth = date.dayOfMonth.toString()
    
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(dayOfWeek, style = MaterialTheme.typography.bodyMedium, color = contentColor.copy(alpha = 0.8f))
        Text(dayOfMonth, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = contentColor)
    }
}

@Composable
private fun TimelineCard(
    title: String,
    subtitle: String,
    timeLabel: String,
    isDone: Boolean,
    onMarkDone: () -> Unit,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(timeLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 4.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!isDone) {
                OutlinedButton(
                    onClick = onMarkDone,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text("Mark done")
                }
            }
        }
    }
}



@Composable
private fun EventDialog(
    event: CalendarEvent?,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, Long, Long, String?) -> Unit,
    onDelete: (Long) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    
    // Default to 9 AM of selected date
    val startInstant = event?.startEpochMillis ?: selectedDate.atTime(9, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endInstant = event?.endEpochMillis ?: selectedDate.atTime(10, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "New Event" else "Edit Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) onSave(title, startInstant, endInstant, location.takeIf { it.isNotBlank() })
                }
            ) { Text("Save") }
        },
        dismissButton = {
            Row {
                if (event != null) {
                    TextButton(onClick = { onDelete(event.id) }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

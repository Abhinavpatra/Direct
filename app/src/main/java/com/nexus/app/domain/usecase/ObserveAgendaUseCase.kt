package com.nexus.app.domain.usecase

import com.nexus.app.core.time.TimeFormatter
import com.nexus.app.domain.model.AgendaDay
import com.nexus.app.domain.repository.CalendarRepository
import com.nexus.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class ObserveAgendaUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val calendarRepository: CalendarRepository,
    private val timeFormatter: TimeFormatter,
) {
    operator fun invoke(): Flow<List<AgendaDay>> {
        val calendarFlow = flow {
            emit(calendarRepository.readUpcomingEvents())
        }
        return combine(taskRepository.observeTasks(), calendarFlow) { tasks, events ->
            val taskGroups = tasks.groupBy { timeFormatter.localDate(it.scheduledAtEpochMillis) }
            val eventGroups = events.groupBy { timeFormatter.localDate(it.startEpochMillis) }
            (taskGroups.keys + eventGroups.keys)
                .toSortedSet()
                .map { date: LocalDate ->
                    AgendaDay(
                        date = date,
                        tasks = taskGroups[date].orEmpty(),
                        calendarEvents = eventGroups[date].orEmpty(),
                    )
                }
        }
    }
}

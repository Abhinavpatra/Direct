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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ObserveAgendaUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val calendarRepository: CalendarRepository,
    private val timeFormatter: TimeFormatter,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(dateRangeFlow: Flow<Pair<LocalDate, LocalDate>>, refreshFlow: Flow<Unit>): Flow<List<AgendaDay>> {
        return combine(dateRangeFlow, refreshFlow) { range, _ -> range }
            .flatMapLatest { (start, end) ->
                val startMillis = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endMillis = end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                
                val calendarFlow = flow {
                    emit(calendarRepository.readEvents(startMillis, endMillis))
                }
                
                combine(taskRepository.observeTasks(), calendarFlow) { tasks, events ->
                    val taskGroups = tasks.groupBy { timeFormatter.localDate(it.scheduledAtEpochMillis) }
                    val eventGroups = events.groupBy { timeFormatter.localDate(it.startEpochMillis) }
                    
                    val daysCount = ChronoUnit.DAYS.between(start, end).toInt() + 1
                    (0 until daysCount).map { i ->
                        val date = start.plusDays(i.toLong())
                        AgendaDay(
                            date = date,
                            tasks = taskGroups[date].orEmpty(),
                            calendarEvents = eventGroups[date].orEmpty(),
                        )
                    }
                }
            }
    }
}

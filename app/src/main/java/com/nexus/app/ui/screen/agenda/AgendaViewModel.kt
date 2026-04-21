package com.nexus.app.ui.screen.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.app.domain.model.TaskStatus
import com.nexus.app.domain.usecase.ObserveAgendaUseCase
import com.nexus.app.domain.usecase.UpdateTaskStatusUseCase
import com.nexus.app.ui.state.AgendaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.nexus.app.domain.usecase.ManageCalendarEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

@HiltViewModel
class AgendaViewModel @Inject constructor(
    observeAgendaUseCase: ObserveAgendaUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val manageCalendarEventUseCase: ManageCalendarEventUseCase,
) : ViewModel() {
    private val _refreshTrigger = MutableStateFlow(Unit)
    private val _dateRange = MutableStateFlow(
        Pair(
            LocalDate.now().minusMonths(1),
            LocalDate.now().plusMonths(3)
        )
    )

    val uiState: StateFlow<AgendaUiState> = observeAgendaUseCase(
        _dateRange.asStateFlow(),
        _refreshTrigger.asStateFlow()
    ).map { AgendaUiState(days = it, isLoading = false) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AgendaUiState(),
        )

    fun markDone(taskId: Long) {
        viewModelScope.launch {
            updateTaskStatusUseCase(taskId, TaskStatus.DONE)
        }
    }

    fun createCalendarEvent(title: String, startEpochMillis: Long, endEpochMillis: Long, location: String?) {
        viewModelScope.launch {
            manageCalendarEventUseCase.create(title, startEpochMillis, endEpochMillis, location)
            _refreshTrigger.value = Unit
        }
    }

    fun updateCalendarEvent(id: Long, title: String, startEpochMillis: Long, endEpochMillis: Long, location: String?) {
        viewModelScope.launch {
            manageCalendarEventUseCase.update(id, title, startEpochMillis, endEpochMillis, location)
            _refreshTrigger.value = Unit
        }
    }

    fun deleteCalendarEvent(id: Long) {
        viewModelScope.launch {
            manageCalendarEventUseCase.delete(id)
            _refreshTrigger.value = Unit
        }
    }

    fun loadMoreDates(startDate: LocalDate, endDate: LocalDate) {
        _dateRange.value = Pair(startDate, endDate)
    }

    fun refreshCalendar() {
        _refreshTrigger.value = Unit
    }
}

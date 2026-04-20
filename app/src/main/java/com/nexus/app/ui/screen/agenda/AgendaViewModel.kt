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

@HiltViewModel
class AgendaViewModel @Inject constructor(
    observeAgendaUseCase: ObserveAgendaUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
) : ViewModel() {
    val uiState: StateFlow<AgendaUiState> = observeAgendaUseCase()
        .map { AgendaUiState(days = it, isLoading = false) }
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
}

package com.nexus.app.ui.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.app.core.result.AppResult
import com.nexus.app.domain.usecase.CreateTaskFromInputUseCase
import com.nexus.app.ui.state.InputUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private val createTaskFromInputUseCase: CreateTaskFromInputUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InputUiState())
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun submit() {
        val currentInput = _uiState.value.input
        if (currentInput.isBlank()) {
            _uiState.update { it.copy(message = "Describe task, time, and reason") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            when (val result = createTaskFromInputUseCase(currentInput)) {
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, message = result.message)
                }
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        input = "",
                        isLoading = false,
                        message = "Saved '${result.value.title}'",
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

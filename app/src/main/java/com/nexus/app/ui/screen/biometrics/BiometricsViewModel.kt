package com.nexus.app.ui.screen.biometrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.app.core.result.AppResult
import com.nexus.app.domain.usecase.ObserveWeightEntriesUseCase
import com.nexus.app.domain.usecase.SaveWeightEntryUseCase
import com.nexus.app.ui.state.BiometricsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricsViewModel @Inject constructor(
    observeWeightEntriesUseCase: ObserveWeightEntriesUseCase,
    private val saveWeightEntryUseCase: SaveWeightEntryUseCase,
) : ViewModel() {
    private val formState = MutableStateFlow(BiometricsUiState())
    val uiState: StateFlow<BiometricsUiState> = combine(
        observeWeightEntriesUseCase(),
        formState,
    ) { entries, form ->
        form.copy(entries = entries)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        BiometricsUiState(),
    )

    fun onWeightChange(value: String) {
        formState.update { it.copy(weightText = value) }
    }

    fun onNoteChange(value: String) {
        formState.update { it.copy(note = value) }
    }

    fun edit(entryId: Long) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        formState.update {
            it.copy(
                editingId = entry.id,
                weightText = entry.weightKg.toString(),
                note = entry.note,
                measuredAtEpochMillis = entry.measuredAtEpochMillis,
                message = "Editing entry",
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            val weight = formState.value.weightText.toFloatOrNull()
                ?: run {
                    formState.update { it.copy(message = "Enter valid weight") }
                    return@launch
                }
            when (
                val result = saveWeightEntryUseCase(
                    formState.value.editingId,
                    weight,
                    formState.value.measuredAtEpochMillis,
                    formState.value.note,
                )
            ) {
                is AppResult.Error -> formState.update { it.copy(message = result.message) }
                is AppResult.Success -> formState.update {
                    it.copy(
                        editingId = 0,
                        weightText = "",
                        note = "",
                        measuredAtEpochMillis = System.currentTimeMillis(),
                        message = "Weight saved",
                    )
                }
            }
        }
    }

    fun clearMessage() {
        formState.update { it.copy(message = null) }
    }
}

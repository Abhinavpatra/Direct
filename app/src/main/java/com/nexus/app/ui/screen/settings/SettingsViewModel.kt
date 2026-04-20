package com.nexus.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.app.core.permission.PermissionTextProvider
import com.nexus.app.core.result.AppResult
import com.nexus.app.domain.usecase.ExportBackupUseCase
import com.nexus.app.domain.usecase.ImportBackupUseCase
import com.nexus.app.ui.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    permissionTextProvider: PermissionTextProvider,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            notificationRationale = permissionTextProvider.notificationRationale(),
            calendarRationale = permissionTextProvider.calendarRationale(),
            exactAlarmRationale = permissionTextProvider.exactAlarmRationale(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun export(uri: Uri) {
        viewModelScope.launch {
            when (val result = exportBackupUseCase(uri)) {
                is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
                is AppResult.Success -> _uiState.update { it.copy(message = "Backup exported") }
            }
        }
    }

    fun import(uri: Uri) {
        viewModelScope.launch {
            when (val result = importBackupUseCase(uri)) {
                is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
                is AppResult.Success -> _uiState.update { it.copy(message = "Backup imported") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

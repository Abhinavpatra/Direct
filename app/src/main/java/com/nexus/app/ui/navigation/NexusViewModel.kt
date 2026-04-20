package com.nexus.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.nexus.app.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NexusViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _showOnboarding = MutableStateFlow(!userPreferencesRepository.isOnboardingCompleted())
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    fun completeOnboarding() {
        userPreferencesRepository.setOnboardingCompleted(true)
        _showOnboarding.value = false
    }
}

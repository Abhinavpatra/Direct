package com.nexus.app.ui.state

import com.nexus.app.domain.model.AgendaDay

data class AgendaUiState(
    val days: List<AgendaDay> = emptyList(),
    val isLoading: Boolean = true,
)

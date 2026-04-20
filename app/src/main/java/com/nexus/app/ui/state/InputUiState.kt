package com.nexus.app.ui.state

import com.nexus.app.domain.model.Task

data class InputUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val history: List<Task> = emptyList(),
)

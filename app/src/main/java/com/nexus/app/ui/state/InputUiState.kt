package com.nexus.app.ui.state

data class InputUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
)

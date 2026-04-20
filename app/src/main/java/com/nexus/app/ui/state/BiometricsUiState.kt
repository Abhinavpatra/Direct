package com.nexus.app.ui.state

import com.nexus.app.domain.model.WeightEntry

data class BiometricsUiState(
    val entries: List<WeightEntry> = emptyList(),
    val editingId: Long = 0,
    val weightText: String = "",
    val note: String = "",
    val measuredAtEpochMillis: Long = System.currentTimeMillis(),
    val message: String? = null,
)

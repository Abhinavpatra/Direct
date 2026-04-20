package com.nexus.app.domain.model

data class WeightEntry(
    val id: Long,
    val weightKg: Float,
    val measuredAtEpochMillis: Long,
    val note: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

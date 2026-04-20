package com.nexus.app.domain.repository

import com.nexus.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun observeEntries(): Flow<List<WeightEntry>>
    suspend fun upsert(entry: WeightEntry): Long
}

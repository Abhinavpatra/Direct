package com.nexus.app.domain.usecase

import com.nexus.app.core.result.AppResult
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.domain.model.WeightEntry
import com.nexus.app.domain.repository.WeightRepository
import javax.inject.Inject

class SaveWeightEntryUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(id: Long, weightKg: Float, measuredAtEpochMillis: Long, note: String): AppResult<Unit> {
        if (weightKg <= 0f) return AppResult.Error("Weight must be greater than zero")
        val now = timeProvider.now().toEpochMilli()
        weightRepository.upsert(
            WeightEntry(
                id = id,
                weightKg = weightKg,
                measuredAtEpochMillis = measuredAtEpochMillis,
                note = note.trim(),
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            ),
        )
        return AppResult.Success(Unit)
    }
}

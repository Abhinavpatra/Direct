package com.nexus.app.domain.usecase

import com.nexus.app.domain.model.WeightEntry
import com.nexus.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWeightEntriesUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {
    operator fun invoke(): Flow<List<WeightEntry>> = weightRepository.observeEntries()
}

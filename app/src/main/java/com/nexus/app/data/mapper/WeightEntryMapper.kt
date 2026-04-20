package com.nexus.app.data.mapper

import com.nexus.app.data.local.entity.WeightEntryEntity
import com.nexus.app.domain.model.WeightEntry
import javax.inject.Inject

class WeightEntryMapper @Inject constructor() {
    fun toDomain(entity: WeightEntryEntity): WeightEntry = WeightEntry(
        id = entity.id,
        weightKg = entity.weightKg,
        measuredAtEpochMillis = entity.measuredAtEpochMillis,
        note = entity.note,
        createdAtEpochMillis = entity.createdAtEpochMillis,
        updatedAtEpochMillis = entity.updatedAtEpochMillis,
    )

    fun toEntity(domain: WeightEntry): WeightEntryEntity = WeightEntryEntity(
        id = domain.id,
        weightKg = domain.weightKg,
        measuredAtEpochMillis = domain.measuredAtEpochMillis,
        note = domain.note,
        createdAtEpochMillis = domain.createdAtEpochMillis,
        updatedAtEpochMillis = domain.updatedAtEpochMillis,
    )
}

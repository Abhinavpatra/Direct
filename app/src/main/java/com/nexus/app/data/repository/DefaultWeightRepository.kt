package com.nexus.app.data.repository

import com.nexus.app.data.local.dao.WeightEntryDao
import com.nexus.app.data.mapper.WeightEntryMapper
import com.nexus.app.domain.model.WeightEntry
import com.nexus.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultWeightRepository @Inject constructor(
    private val weightEntryDao: WeightEntryDao,
    private val weightEntryMapper: WeightEntryMapper,
) : WeightRepository {
    override fun observeEntries(): Flow<List<WeightEntry>> =
        weightEntryDao.observeAll().map { items -> items.map(weightEntryMapper::toDomain) }

    override suspend fun upsert(entry: WeightEntry): Long {
        val entity = weightEntryMapper.toEntity(entry)
        return if (entry.id == 0L) {
            weightEntryDao.insert(entity)
        } else {
            weightEntryDao.update(entity)
            entry.id
        }
    }
}

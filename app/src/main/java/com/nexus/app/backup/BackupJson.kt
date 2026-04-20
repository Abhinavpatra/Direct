package com.nexus.app.backup

import com.nexus.app.data.local.entity.TaskEntity
import com.nexus.app.data.local.entity.WeightEntryEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupJson(
    val schemaVersion: Int = 1,
    val tasks: List<BackupTask>,
    val weightEntries: List<BackupWeightEntry>,
)

@Serializable
data class BackupTask(
    val id: Long,
    val title: String,
    val scheduledAtEpochMillis: Long,
    val reason: String,
    val actionType: String,
    val status: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
data class BackupWeightEntry(
    val id: Long,
    val weightKg: Float,
    val measuredAtEpochMillis: Long,
    val note: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

fun TaskEntity.toBackup(): BackupTask = BackupTask(
    id = id,
    title = title,
    scheduledAtEpochMillis = scheduledAtEpochMillis,
    reason = reason,
    actionType = actionType,
    status = status,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

fun WeightEntryEntity.toBackup(): BackupWeightEntry = BackupWeightEntry(
    id = id,
    weightKg = weightKg,
    measuredAtEpochMillis = measuredAtEpochMillis,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

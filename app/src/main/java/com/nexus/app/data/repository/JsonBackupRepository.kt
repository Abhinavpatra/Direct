package com.nexus.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.nexus.app.backup.BackupJson
import com.nexus.app.backup.toBackup
import com.nexus.app.core.result.AppResult
import com.nexus.app.data.local.database.NexusDatabase
import com.nexus.app.data.local.entity.TaskEntity
import com.nexus.app.data.local.entity.WeightEntryEntity
import com.nexus.app.domain.repository.BackupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class JsonBackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: NexusDatabase,
    private val json: Json,
) : BackupRepository {
    override suspend fun exportTo(uri: Uri): AppResult<Unit> = try {
        val payload = BackupJson(
            tasks = database.taskDao().getAll().map(TaskEntity::toBackup),
            weightEntries = database.weightEntryDao().getAll().map(WeightEntryEntity::toBackup),
        )
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
            writer.write(json.encodeToString(BackupJson.serializer(), payload))
        } ?: return AppResult.Error("Unable to open export destination")
        AppResult.Success(Unit)
    } catch (error: Exception) {
        AppResult.Error("Backup export failed", error)
    }

    override suspend fun importFrom(uri: Uri): AppResult<Unit> = try {
        val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            ?: return AppResult.Error("Unable to read backup file")
        val payload = json.decodeFromString(BackupJson.serializer(), content)
        if (payload.schemaVersion != 1) return AppResult.Error("Unsupported backup schema")
        database.withTransaction {
            payload.tasks.forEach { task ->
                database.taskDao().insert(
                    TaskEntity(
                        id = task.id,
                        title = task.title,
                        scheduledAtEpochMillis = task.scheduledAtEpochMillis,
                        reason = task.reason,
                        actionType = task.actionType,
                        status = task.status,
                        createdAtEpochMillis = task.createdAtEpochMillis,
                        updatedAtEpochMillis = task.updatedAtEpochMillis,
                    ),
                )
            }
            payload.weightEntries.forEach { entry ->
                database.weightEntryDao().insert(
                    WeightEntryEntity(
                        id = entry.id,
                        weightKg = entry.weightKg,
                        measuredAtEpochMillis = entry.measuredAtEpochMillis,
                        note = entry.note,
                        createdAtEpochMillis = entry.createdAtEpochMillis,
                        updatedAtEpochMillis = entry.updatedAtEpochMillis,
                    ),
                )
            }
        }
        AppResult.Success(Unit)
    } catch (error: SerializationException) {
        AppResult.Error("Backup file is invalid JSON", error)
    } catch (error: Exception) {
        AppResult.Error("Backup import failed", error)
    }
}

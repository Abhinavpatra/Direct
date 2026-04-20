package com.nexus.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexus.app.data.local.dao.TaskDao
import com.nexus.app.data.local.dao.WeightEntryDao
import com.nexus.app.data.local.entity.TaskEntity
import com.nexus.app.data.local.entity.WeightEntryEntity

@Database(
    entities = [TaskEntity::class, WeightEntryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NexusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun weightEntryDao(): WeightEntryDao
}

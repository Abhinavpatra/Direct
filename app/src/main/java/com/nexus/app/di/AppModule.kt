package com.nexus.app.di

import android.content.Context
import androidx.room.Room
import com.nexus.app.backup.BackupJson
import com.nexus.app.calendar.CalendarContentRepository
import com.nexus.app.core.dispatcher.DefaultDispatcherProvider
import com.nexus.app.core.dispatcher.DispatcherProvider
import com.nexus.app.core.time.DefaultTimeProvider
import com.nexus.app.core.time.TimeProvider
import com.nexus.app.data.local.dao.TaskDao
import com.nexus.app.data.local.dao.WeightEntryDao
import com.nexus.app.data.local.database.NexusDatabase
import com.nexus.app.data.mapper.TaskMapper
import com.nexus.app.data.mapper.WeightEntryMapper
import com.nexus.app.data.repository.DefaultTaskRepository
import com.nexus.app.data.repository.DefaultWeightRepository
import com.nexus.app.data.repository.FirebaseAiTaskParserRepository
import com.nexus.app.data.repository.JsonBackupRepository
import com.nexus.app.domain.repository.AiTaskParserRepository
import com.nexus.app.domain.repository.BackupRepository
import com.nexus.app.domain.repository.CalendarRepository
import com.nexus.app.domain.repository.TaskRepository
import com.nexus.app.domain.repository.WeightRepository
import com.nexus.app.reminder.alarm.AlarmReminderScheduler
import com.nexus.app.reminder.alarm.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NexusDatabase =
        Room.databaseBuilder(
            context,
            NexusDatabase::class.java,
            "nexus.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    fun provideTaskDao(database: NexusDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideWeightEntryDao(database: NexusDatabase): WeightEntryDao = database.weightEntryDao()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule {
    @Binds
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider

    @Binds
    abstract fun bindTimeProvider(impl: DefaultTimeProvider): TimeProvider

    @Binds
    abstract fun bindTaskRepository(impl: DefaultTaskRepository): TaskRepository

    @Binds
    abstract fun bindWeightRepository(impl: DefaultWeightRepository): WeightRepository

    @Binds
    abstract fun bindAiTaskParserRepository(impl: com.nexus.app.data.repository.HybridAiTaskParserRepository): AiTaskParserRepository

    @Binds
    abstract fun bindCalendarRepository(impl: CalendarContentRepository): CalendarRepository

    @Binds
    abstract fun bindReminderScheduler(impl: AlarmReminderScheduler): ReminderScheduler

    @Binds
    abstract fun bindBackupRepository(impl: JsonBackupRepository): BackupRepository
}

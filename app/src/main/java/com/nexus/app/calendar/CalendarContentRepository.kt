package com.nexus.app.calendar

import android.content.Context
import android.provider.CalendarContract
import com.nexus.app.domain.model.CalendarEvent
import com.nexus.app.domain.repository.CalendarRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject

class CalendarContentRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : CalendarRepository {
    override suspend fun readUpcomingEvents(limit: Int): List<CalendarEvent> {
        val now = Instant.now().toEpochMilli()
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(now.toString())
            .appendPath((now + 7 * 24 * 60 * 60 * 1000).toString())
            .build()
        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.EVENT_LOCATION,
        )
        return buildList {
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC",
            )?.use { cursor ->
                while (cursor.moveToNext() && size < limit) {
                    add(
                        CalendarEvent(
                            id = cursor.getLong(0),
                            title = cursor.getString(1).orEmpty().ifBlank { "Untitled event" },
                            startEpochMillis = cursor.getLong(2),
                            endEpochMillis = cursor.getLong(3),
                            location = cursor.getString(4),
                        ),
                    )
                }
            }
        }
    }
}

package com.nexus.app.calendar

import android.content.Context
import android.provider.CalendarContract
import com.nexus.app.domain.model.CalendarEvent
import com.nexus.app.domain.repository.CalendarRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject

import android.content.ContentUris
import android.content.ContentValues
import java.util.TimeZone

class CalendarContentRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : CalendarRepository {

    override suspend fun readEvents(startEpochMillis: Long, endEpochMillis: Long): List<CalendarEvent> {
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(startEpochMillis.toString())
            .appendPath(endEpochMillis.toString())
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
                while (cursor.moveToNext()) {
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

    private fun getPrimaryCalendarId(): Long? {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.VISIBLE} = 1 AND ${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ${CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR}"
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(0)
            }
        }
        return null
    }

    override suspend fun insertEvent(
        title: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
        location: String?
    ): Long {
        val calId = getPrimaryCalendarId() ?: return -1L
        
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startEpochMillis)
            put(CalendarContract.Events.DTEND, endEpochMillis)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
        
        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLongOrNull() ?: -1L
    }

    override suspend fun updateEvent(
        id: Long,
        title: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
        location: String?
    ) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startEpochMillis)
            put(CalendarContract.Events.DTEND, endEpochMillis)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id)
        context.contentResolver.update(updateUri, values, null, null)
    }

    override suspend fun deleteEvent(id: Long) {
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id)
        context.contentResolver.delete(deleteUri, null, null)
    }
}

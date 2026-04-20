package com.nexus.app.domain.model

data class Task(
    val id: Long,
    val title: String,
    val scheduledAtEpochMillis: Long,
    val reason: String,
    val actionType: ActionType,
    val status: TaskStatus,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

enum class ActionType {
    REMINDER,
    CALL,
    MEETING,
    OTHER,
    ;

    companion object {
        fun fromRaw(raw: String): ActionType? = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
    }
}

enum class TaskStatus {
    PENDING,
    DONE,
    CANCELED,
}

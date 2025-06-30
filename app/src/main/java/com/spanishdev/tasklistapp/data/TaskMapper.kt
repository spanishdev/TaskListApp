package com.spanishdev.tasklistapp.data

import com.spanishdev.tasklistapp.database.entities.TaskEntity
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskMapper(private val dateFormat: DateFormat) {

    fun toEntity(task: Task): TaskEntity = TaskEntity(
        id = task.id,
        name = task.name,
        description = task.description,
        status = task.status.toDbFormat(),
        createdAt = task.createdAt.parseDate()
    )

    fun toDomain(taskEntity: TaskEntity?): Task? = taskEntity?.let { entity ->
        Task(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            status = entity.status.toDomain(),
            createdAt = entity.createdAt.formatDate()
        )
    }

    private fun Status.toDbFormat() = when (this) {
        Status.Pending -> "PENDING"
        Status.InProgress -> "IN_PROGRESS"
        Status.Done -> "DONE"
        Status.Cancelled -> "CANCELLED"
    }

    private fun String.toDomain() = when (this) {
        "PENDING" -> Status.Pending
        "IN_PROGRESS" -> Status.InProgress
        "DONE" -> Status.Done
        "CANCELLED" -> Status.Cancelled
        else -> Status.Pending
    }

    private fun Long.formatDate(): String {
        return dateFormat.format(Date(this))
    }

    private fun String.parseDate(): Long {
        return try {
            dateFormat.parse(this)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            // Fallback
            System.currentTimeMillis()
        }
    }
}
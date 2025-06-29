package com.spanishdev.tasklistapp.data

import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.database.entities.TaskEntity
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {

    override suspend fun addTask(task: Task): Long =
        taskDao.insertTask(task.toEntity())

    override suspend fun deleteTask(task: Task): Boolean {
        val rowsAffected = taskDao.deleteTask(task.toEntity())
        return rowsAffected > 0
    }

    override suspend fun updateTask(task: Task): Boolean {
        val rowsAffected = taskDao.updateTask(task.toEntity())
        return rowsAffected > 0
    }

    override suspend fun getTasks(): List<Task> =
        taskDao.getAllTasks().firstOrNull()?.map { it.toDomain() } ?: emptyList()

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)?.toDomain()
}

private fun Task.toEntity() = TaskEntity(
    id = id,
    name = name,
    description = description,
    status = status.toDbFormat(),
)

private fun TaskEntity.toDomain() = Task(
    id = id,
    name = name,
    description = description,
    status = status.toDomain(),
)

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
package com.spanishdev.tasklistapp.data

import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.database.entities.TaskEntity
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first

class TaskRepositoryImpl(val taskDao: TaskDao): TaskRepository {
    override suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun getTasks(): List<Task> =
        taskDao.getAllTasks().first().map { it.toDomain() }

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

private fun Status.toDbFormat() = when(this) {
    Status.Pending -> "PENDING"
    Status.InProgress -> "IN_PROGRESS"
    Status.Done -> "DONE"
    Status.Cancelled -> "CANCELLED"
}

private fun String.toDomain() = when(this) {
    "PENDING" -> Status.Pending
    "IN_PROGRESS" -> Status.InProgress
    "DONE" -> Status.Done
    "CANCELLED" -> Status.Cancelled
    else -> Status.Pending
}
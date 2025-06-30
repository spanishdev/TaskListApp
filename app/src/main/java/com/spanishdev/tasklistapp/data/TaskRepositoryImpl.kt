package com.spanishdev.tasklistapp.data

import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.database.entities.TaskEntity
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper,
) : TaskRepository {

    override suspend fun addTask(task: Task): Long =
        taskDao.insertTask(taskMapper.toEntity(task))

    override suspend fun deleteTask(task: Task): Boolean {
        val rowsAffected = taskDao.deleteTask(taskMapper.toEntity(task))
        return rowsAffected > 0
    }

    override suspend fun updateTask(task: Task): Boolean {
        val rowsAffected = taskDao.updateTask(taskMapper.toEntity(task))
        return rowsAffected > 0
    }

    override suspend fun getTasks(): List<Task> =
        taskDao.getAllTasks().firstOrNull()?.mapNotNull(taskMapper::toDomain) ?: emptyList()

    override suspend fun getTaskById(id: Long): Task? =
        taskMapper.toDomain(taskDao.getTaskById(id))
}

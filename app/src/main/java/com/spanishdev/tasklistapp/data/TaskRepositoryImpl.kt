package com.spanishdev.tasklistapp.data

import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TaskRepository {

    override suspend fun addTask(task: Task): Long =
        taskDao.insertTask(taskMapper.toEntity(task))

    override suspend fun deleteTasks(tasks: List<Long>): Boolean {
        val rowsAffected = taskDao.deleteTasksByIds(tasks)
        return rowsAffected > 0
    }

    override suspend fun updateTask(task: Task): Boolean {
        val rowsAffected = taskDao.updateTask(taskMapper.toEntity(task))
        return rowsAffected > 0
    }

    override fun getTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities ->
            entities.mapNotNull(taskMapper::toDomain)
        }.flowOn(dispatcher)

    override suspend fun getTaskById(id: Long): Task? =
        taskMapper.toDomain(taskDao.getTaskById(id))
}

package com.spanishdev.tasklistapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.database.entities.TaskEntity
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import com.spanishdev.tasklistapp.domain.repository.TaskRepository.TaskSort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
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

    override fun getTasks(sorting: TaskSort): Flow<PagingData<Task>> {
        val pagingSourceFactory: () -> PagingSource<Int, TaskEntity> = when (sorting) {
            TaskSort.CREATE_DATE -> {
                { taskDao.getAllTasksByCreatedAtAsc() }
            }

            TaskSort.NAME -> {
                { taskDao.getAllTasksByName() }
            }

            TaskSort.STATUS -> {
                { taskDao.getAllTasksByStatus() }
            }
        }

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> taskMapper.toDomain(entity) }
            }
            .filterNotNull()
            .flowOn(dispatcher)
    }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)?.let(taskMapper::toDomain)


    companion object {
        const val PAGE_SIZE = 10
    }
}

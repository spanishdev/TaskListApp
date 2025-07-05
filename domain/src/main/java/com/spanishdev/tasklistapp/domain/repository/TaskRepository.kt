package com.spanishdev.tasklistapp.domain.repository

import androidx.paging.PagingData
import com.spanishdev.tasklistapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    enum class TaskSort {
        CREATE_DATE,
        NAME,
        STATUS,
    }

    fun getTasks(sorting: TaskSort): Flow<PagingData<Task>>

    suspend fun addTask(task: Task): Long
    suspend fun deleteTasks(tasks: List<Long>): Boolean
    suspend fun updateTask(task: Task): Boolean
    suspend fun getTaskById(id: Long): Task?
}

package com.spanishdev.tasklistapp.domain.repository

import com.spanishdev.tasklistapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    enum class TaskSort {
        CREATE_DATE,
        NAME,
        STATUS,
    }

    fun getTasks(sorting: TaskSort): Flow<List<Task>>

    suspend fun addTask(task: Task): Long
    suspend fun deleteTasks(tasks: List<Long>): Boolean
    suspend fun updateTask(task: Task): Boolean
    suspend fun getTaskById(id: Long): Task?
}

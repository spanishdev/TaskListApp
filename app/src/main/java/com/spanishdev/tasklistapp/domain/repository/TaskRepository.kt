package com.spanishdev.tasklistapp.domain.repository

import com.spanishdev.tasklistapp.domain.model.Task

interface TaskRepository {
    suspend fun addTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun getTasks(): List<Task>
    suspend fun getTaskById(id: Long): Task?
}

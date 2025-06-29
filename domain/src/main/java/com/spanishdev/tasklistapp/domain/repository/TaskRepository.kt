package com.spanishdev.tasklistapp.domain.repository

import com.spanishdev.tasklistapp.domain.model.Task

interface TaskRepository {
    suspend fun addTask(task: Task): Long
    suspend fun deleteTask(task: Task): Boolean
    suspend fun updateTask(task: Task): Boolean
    suspend fun getTasks(): List<Task>
    suspend fun getTaskById(id: Long): Task?
}

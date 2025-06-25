package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.repository.TaskRepository

class GetTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke() = repository.getTasks()
}
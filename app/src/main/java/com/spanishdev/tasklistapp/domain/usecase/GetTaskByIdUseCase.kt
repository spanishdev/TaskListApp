package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.repository.TaskRepository

class GetTaskByIdUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.getTaskById(id)
}
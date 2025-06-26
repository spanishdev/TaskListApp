package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.entities.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.addTask(task)
}

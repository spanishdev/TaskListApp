package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateTaskUseCase(
    private val repository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(task: Task) = withContext(dispatcher) {
        require(task.id > 0) { "Task id must be greater than 0" }
        require(task.name.isNotBlank()) { "Task name cannot be blank" }

        repository.updateTask(task)
    }
}

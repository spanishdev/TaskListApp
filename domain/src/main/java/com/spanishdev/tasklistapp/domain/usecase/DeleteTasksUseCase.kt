package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTasksUseCase(
    private val repository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(tasks: List<Long>) = withContext(dispatcher) {
        repository.deleteTasks(tasks)
    }
}

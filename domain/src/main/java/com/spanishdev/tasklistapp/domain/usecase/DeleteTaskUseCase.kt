package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(task: Task) = withContext(dispatcher) {
        repository.deleteTask(task)
    }
}

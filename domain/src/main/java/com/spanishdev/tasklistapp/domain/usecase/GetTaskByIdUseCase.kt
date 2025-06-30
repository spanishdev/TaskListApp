package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id: Long) = withContext(dispatcher) {
        repository.getTaskById(id)
    }
}

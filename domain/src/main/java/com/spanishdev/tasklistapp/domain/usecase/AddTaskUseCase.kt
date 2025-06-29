package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddTaskUseCase(
    private val repository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(name: String, description: String) = withContext(dispatcher) {
        require(name.isNotBlank()) { "Task name cannot be blank" }

        val newTask = Task(
            id = 0L,
            name = name.trim(),
            description = description.trim(),
            status = Status.Pending
        )

        val id = repository.addTask(newTask)
        return@withContext newTask.copy(id = id)
    }
}

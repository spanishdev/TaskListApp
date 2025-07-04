package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTaskUseCase(
    private val repository: TaskRepository,
    private val dataFormatter: DateFormat = SimpleDateFormat(
        "dd-MM-yyyy HH:mm",
        Locale.getDefault()
    ),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(
        name: String,
        description: String,
    ) = withContext(dispatcher) {
        if (name.isBlank()) {
            throw InvalidTaskNameException()
        }
        if (description.isBlank()) {
            throw InvalidTaskDescriptionException()
        }

        val newTask = Task(
            id = 0L,
            name = name.trim(),
            description = description.trim(),
            status = Status.Pending,
            createdAt = dataFormatter.format(Date(System.currentTimeMillis()))
        )

        val id = repository.addTask(newTask)
        return@withContext newTask.copy(id = id)
    }

    data class InvalidTaskNameException(
        override val message: String = "Task name cannot be blank"
    ) : Exception()

    data class InvalidTaskDescriptionException(
        override val message: String = "Task description cannot be blank"
    ) : Exception()
}

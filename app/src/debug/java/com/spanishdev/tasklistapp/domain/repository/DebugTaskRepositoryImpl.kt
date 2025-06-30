package com.spanishdev.tasklistapp.domain.repository

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task

class DebugTaskRepositoryImpl : TaskRepository {

    private val taskList: MutableList<Task> = mutableListOf(
        Task(
            id = 1L,
            name = "Buy milk",
            description = "Go to supermarket and pickup 2 bottles",
            status = Status.InProgress,
            createdAt = "22-06-2025 18:00"
        ),
        Task(
            id = 2L,
            name = "Do chores",
            description = "Wash clothes and sweep the floor",
            status = Status.Pending,
            createdAt = "19-06-2025 11:00"
        ),
        Task(
            id = 3L,
            name = "Prepare lunch",
            description = "Cook some nice rice",
            status = Status.Pending,
            createdAt = "14-06-2025 12:00"
        )
    )

    override suspend fun addTask(task: Task): Long {
        taskList.add(task)
        return task.id
    }

    override suspend fun deleteTask(task: Task): Boolean {
        return taskList.remove(task)
    }

    override suspend fun updateTask(task: Task): Boolean {
        val index = taskList.indexOf(task)
        return if (index >= 0) {
            taskList[index] = task
            true
        } else {
            false
        }
    }

    override suspend fun getTasks(): List<Task> {
        return taskList
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskList.find { it.id == id }
    }
}

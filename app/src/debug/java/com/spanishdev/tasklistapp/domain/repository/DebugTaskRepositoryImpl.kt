package com.spanishdev.tasklistapp.domain.repository

import com.spanishdev.tasklistapp.domain.entities.Status
import com.spanishdev.tasklistapp.domain.entities.Task

class DebugTaskRepositoryImpl : TaskRepository {

    private val taskList: MutableList<Task> = mutableListOf(
        Task(
            id = 1L,
            name = "Buy milk",
            description = "Go to supermarket and pickup 2 bottles",
            status = Status.InProgress,
        ),
        Task(
            id = 2L,
            name = "Do chores",
            description = "Wash clothes and sweep the floor",
            status = Status.Pending,
        ),
        Task(
            id = 3L,
            name = "Prepare lunch",
            description = "Cook some nice rice",
            status = Status.Pending,
        )
    )

    override suspend fun addTask(task: Task) {
        taskList.add(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskList.remove(task)
    }

    override suspend fun updateTask(task: Task) {
        val index = taskList.indexOf(task)
        if (index >= 0) {
            taskList[index] = task
        }
    }

    override suspend fun getTasks(): List<Task> {
        return taskList
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskList.find { it.id == id }
    }
}

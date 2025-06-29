package com.spanishdev.tasklistapp.domain.repository

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task

class DebugTaskRepositoryImpl : com.spanishdev.tasklistapp.domain.repository.TaskRepository {

    private val taskList: MutableList<com.spanishdev.tasklistapp.domain.model.Task> = mutableListOf(
        com.spanishdev.tasklistapp.domain.model.Task(
            id = 1L,
            name = "Buy milk",
            description = "Go to supermarket and pickup 2 bottles",
            status = com.spanishdev.tasklistapp.domain.model.Status.InProgress,
        ),
        com.spanishdev.tasklistapp.domain.model.Task(
            id = 2L,
            name = "Do chores",
            description = "Wash clothes and sweep the floor",
            status = com.spanishdev.tasklistapp.domain.model.Status.Pending,
        ),
        com.spanishdev.tasklistapp.domain.model.Task(
            id = 3L,
            name = "Prepare lunch",
            description = "Cook some nice rice",
            status = com.spanishdev.tasklistapp.domain.model.Status.Pending,
        )
    )

    override suspend fun addTask(task: com.spanishdev.tasklistapp.domain.model.Task) {
        taskList.add(task)
    }

    override suspend fun deleteTask(task: com.spanishdev.tasklistapp.domain.model.Task) {
        taskList.remove(task)
    }

    override suspend fun updateTask(task: com.spanishdev.tasklistapp.domain.model.Task) {
        val index = taskList.indexOf(task)
        if (index >= 0) {
            taskList[index] = task
        }
    }

    override suspend fun getTasks(): List<com.spanishdev.tasklistapp.domain.model.Task> {
        return taskList
    }

    override suspend fun getTaskById(id: Long): com.spanishdev.tasklistapp.domain.model.Task? {
        return taskList.find { it.id == id }
    }
}

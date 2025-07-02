package com.spanishdev.tasklistapp.navigation

import com.spanishdev.tasklistapp.domain.model.Task
import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {

    @Serializable
    data object TaskList: AppRoutes()

    @Serializable
    data object AddTask: AppRoutes()

    @Serializable
    data class EditTask(val taskId: Long): AppRoutes()
}
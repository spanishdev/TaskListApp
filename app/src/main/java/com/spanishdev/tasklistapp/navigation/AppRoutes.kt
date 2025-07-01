package com.spanishdev.tasklistapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {

    @Serializable
    data object TaskList: AppRoutes()

    @Serializable
    data object AddTask: AppRoutes()
}
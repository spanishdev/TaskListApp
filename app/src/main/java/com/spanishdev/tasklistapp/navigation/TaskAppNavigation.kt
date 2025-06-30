package com.spanishdev.tasklistapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.spanishdev.tasklistapp.ui.addtask.AddTaskScreen
import com.spanishdev.tasklistapp.ui.addtask.AddTaskViewModel
import com.spanishdev.tasklistapp.ui.tasklist.TaskListScreen
import com.spanishdev.tasklistapp.ui.tasklist.TaskListViewModel

@Composable
fun TaskAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.TaskList
    ) {
        composable<AppRoutes.TaskList> {
            val viewModel = hiltViewModel<TaskListViewModel>()

            TaskListScreen(
                viewModel = viewModel,
                onAddTaskNavigation = {
                    navController.navigate(AppRoutes.AddTask)
                }
            )
        }

        composable<AppRoutes.AddTask> {
            val viewModel = hiltViewModel<AddTaskViewModel>()

            AddTaskScreen(
                viewModel = viewModel,
                onBackNavigation = {
                    navController.popBackStack()
                },
                onSuccessNavigation = {
                    navController.popBackStack()
                }
            )
        }
    }
}
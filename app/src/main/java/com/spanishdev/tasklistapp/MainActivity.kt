package com.spanishdev.tasklistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.spanishdev.tasklistapp.ui.theme.TaskListAppTheme
import com.spanishdev.tasklistapp.ui.view.TaskListScreen
import com.spanishdev.tasklistapp.ui.viewmodel.TaskListViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaskListViewModel by viewModels {
        throw NotImplementedError("Later will be added a ViewModelProvider.Factory")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskListAppTheme {
                TaskListScreen(viewModel = viewModel)
            }
        }
    }
}

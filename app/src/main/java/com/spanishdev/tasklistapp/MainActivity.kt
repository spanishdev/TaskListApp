package com.spanishdev.tasklistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.spanishdev.tasklistapp.navigation.TaskAppNavigation
import com.spanishdev.tasklistapp.theme.TaskListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskListAppTheme {
                TaskAppNavigation()
            }
        }
    }
}

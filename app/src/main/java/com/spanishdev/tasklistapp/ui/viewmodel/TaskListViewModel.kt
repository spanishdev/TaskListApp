package com.spanishdev.tasklistapp.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spanishdev.tasklistapp.domain.entities.Task
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(private val getTasksUseCase: GetTasksUseCase) : ViewModel() {

    @Immutable
    sealed class State {
        data object Loading : State()
        data object Empty : State()
        data class Success(val tasks: List<Task>) : State()
        data class Error(val message: String) : State()
    }

    private val _uiState = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _uiState

    init {
        fetchTasks()
    }

    fun fetchTasks() {
        viewModelScope.launch {
            _uiState.value = State.Loading

            try {
                val tasks = getTasksUseCase()
                _uiState.value = if (tasks.isEmpty()) {
                    State.Empty
                } else {
                    State.Success(tasks)
                }

            } catch (e: Exception) {
                _uiState.value = State.Error(e.message ?: "Unknown error")
            }
        }
    }

}
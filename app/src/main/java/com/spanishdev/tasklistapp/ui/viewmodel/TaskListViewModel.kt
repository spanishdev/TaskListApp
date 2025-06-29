package com.spanishdev.tasklistapp.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val state: StateFlow<State> = _uiState.asStateFlow()

    private val _isRefreshingState = MutableStateFlow<Boolean>(false)
    val isRefreshingState: StateFlow<Boolean> = _isRefreshingState.asStateFlow()

    init {
        fetchTasks()
    }

    fun refreshTasks() = loadTasks(isLoading = false, isRefreshing = true)
    private fun fetchTasks() = loadTasks(isLoading = true, isRefreshing = false)

    private fun loadTasks(
        isLoading: Boolean = false,
        isRefreshing: Boolean = false,
    ) {
        viewModelScope.launch {
            if (isLoading && _uiState.value !is State.Success) {
                _uiState.value = State.Loading
            }

            if (isRefreshing) {
                _isRefreshingState.value = true
            }

            try {
                val tasks = getTasksUseCase()

                if (isRefreshing) {
                    //Ensure pullToRefresh animation visibility
                    delay(300)
                }

                _uiState.value = if (tasks.isEmpty()) {
                    State.Empty
                } else {
                    State.Success(tasks)
                }
            } catch (e: Exception) {
                _uiState.value = State.Error(e.message ?: "Unknown error")
            } finally {
                if (isRefreshing) {
                    _isRefreshingState.value = false
                }
            }
        }
    }
}
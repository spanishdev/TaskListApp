package com.spanishdev.tasklistapp.ui.tasklist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.usecase.DeleteTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
    getTasksUseCase: GetTasksUseCase,
) : ViewModel() {

    @Immutable
    sealed class State {
        data object Loading : State()
        data object Empty : State()
        data class Success(val tasks: List<Task>) : State()
        data class Error(val message: String) : State()
    }

    sealed class Event {
        data class UpdateTask(val task: Task) : Event()
        data object Refresh : Event()
    }

    val state: StateFlow<State> = getTasksUseCase()
        .map { tasks ->
            if(tasks.isEmpty()) State.Empty else State.Success(tasks)
        }
        .catch { error ->
            State.Error(error.message ?: "Unknown error")
        }
        .onCompletion { _isRefreshingState.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = State.Loading
        )

    private val _isRefreshingState = MutableStateFlow(false)
    val isRefreshingState: StateFlow<Boolean> = _isRefreshingState.asStateFlow()

    fun sendEvent(event: Event) = when (event) {
        is Event.Refresh -> refreshTasks()
        is Event.UpdateTask -> updateTask(event.task)
    }

    private fun refreshTasks() {
        if(_isRefreshingState.value) return
        //Not needed as state updates automatically. May be needed for Backend implementation.
        viewModelScope.launch {
            _isRefreshingState.value = true
            delay(300)  // Simulatees load
            _isRefreshingState.value = false
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
        }
    }
}
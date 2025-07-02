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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
        data class Loaded(
            val tasks: List<Task>,
            val selected: Set<Long>,
        ) : State()

        data class Error(val message: String) : State()
    }

    sealed class Event {
        data class UpdateTask(val task: Task) : Event()
        data class SelectTask(val taskId: Long, val selected: Boolean) : Event()
        data object ClearSelection : Event()
        data object DeleteSelectedTasks : Event()
        data object Refresh : Event()
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getTasksUseCase()
                .catch { error ->
                    _state.value = State.Error(error.message ?: "Unknown error")
                    _isRefreshingState.value = false
                }
                .collect { tasks ->
                    val currentSelected = (state.value as? State.Loaded)?.selected ?: emptySet()
                    val filteredSelected = currentSelected.filter { id ->
                        tasks.any { it.id == id }
                    }.toSet()

                    _state.value = if (tasks.isEmpty()) {
                        State.Empty
                    } else {
                        State.Loaded(tasks, filteredSelected)
                    }
                    _isRefreshingState.value = false
                }
        }
    }


    private val _isRefreshingState = MutableStateFlow(false)
    val isRefreshingState: StateFlow<Boolean> = _isRefreshingState.asStateFlow()

    fun sendEvent(event: Event) = when (event) {
        is Event.Refresh -> refreshTasks()
        is Event.UpdateTask -> updateTask(event.task)
        is Event.DeleteSelectedTasks -> deleteSelectedTasks()
        is Event.SelectTask -> handleSelectTask(event.taskId, event.selected)
        is Event.ClearSelection -> handleSelectionClear()
    }

    private fun refreshTasks() {
        if (_isRefreshingState.value) return
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

    private fun deleteSelectedTasks() {
        viewModelScope.launch {
            (state.value as? State.Loaded)?.let { taskState ->
                val selected = taskState.selected
                deleteTasksUseCase(selected.toList())
                _state.value = taskState.copy(selected = emptySet())
            }
        }
    }

    private fun handleSelectTask(taskId: Long, isSelected: Boolean) {
        (state.value as? State.Loaded)?.let { taskState ->
            val selectedTasks = taskState.selected.toMutableSet()

            if (isSelected) {
                selectedTasks.add(taskId)
            } else {
                selectedTasks.remove(taskId)
            }

            _state.value = taskState.copy(selected = selectedTasks)
        }
    }

    private fun handleSelectionClear() {
        (state.value as? State.Loaded)?.let { taskState ->
            _state.value = taskState.copy(selected = emptySet())
        }
    }
}
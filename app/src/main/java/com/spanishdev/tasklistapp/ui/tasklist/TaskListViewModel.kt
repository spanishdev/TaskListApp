package com.spanishdev.tasklistapp.ui.tasklist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository.TaskSort
import com.spanishdev.tasklistapp.domain.usecase.DeleteTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _isRefreshingState = MutableStateFlow(false)
    val isRefreshingState: StateFlow<Boolean> = _isRefreshingState.asStateFlow()

    private val _sortingState = MutableStateFlow(TaskSort.CREATE_DATE)
    val sortingState: StateFlow<TaskSort> = _sortingState.asStateFlow()

    private var observeTasksJob: Job? = null

    constructor(
        getTasksUseCase: GetTasksUseCase,
        updateTaskUseCase: UpdateTaskUseCase,
        deleteTasksUseCase: DeleteTasksUseCase,
        initialState: State
    ) : this(getTasksUseCase, updateTaskUseCase, deleteTasksUseCase) {
        _state.value = initialState
        observeTasks()
    }

    @Immutable
    sealed class State {
        data object Loading : State()
        data object Empty : State()
        data class Loaded(
            val tasks: Flow<PagingData<Task>>,
            val selected: Set<Long>,
        ) : State()

        data class Error(val message: String) : State()
    }

    @Immutable
    sealed class Event {
        data class UpdateTask(val task: Task) : Event()
        data class SelectTask(val taskId: Long, val selected: Boolean) : Event()
        data object ClearSelection : Event()
        data object DeleteSelectedTasks : Event()
        data object Refresh : Event()
        data class OrderSelected(val sorting: TaskSort) : Event()
    }

    init {
        observeTasks()
    }

    private fun observeTasks() {
        observeTasksJob?.cancel()

        observeTasksJob = viewModelScope.launch {
            _sortingState.collectLatest { sorting ->
                getTasksUseCase(sorting)
                    .cachedIn(viewModelScope)
                    .catch { error ->
                        _state.value = State.Error(error.message ?: "Unknown error")
                        _isRefreshingState.value = false
                    }
                    .collect { pagingData ->
                        val currentSelected = (state.value as? State.Loaded)?.selected ?: emptySet()

                        _state.value = State.Loaded(
                            tasks = flowOf(pagingData),
                            selected = currentSelected
                        )
                        _isRefreshingState.value = false
                    }
            }
        }
    }

    fun sendEvent(event: Event) = when (event) {
        is Event.Refresh -> refreshTasks()
        is Event.UpdateTask -> updateTask(event.task)
        is Event.DeleteSelectedTasks -> deleteSelectedTasks()
        is Event.SelectTask -> handleSelectTask(event.taskId, event.selected)
        is Event.ClearSelection -> handleSelectionClear()
        is Event.OrderSelected -> handleOrderSelcted(event.sorting)
    }

    private fun handleOrderSelcted(sorting: TaskSort) {
        _sortingState.update { sorting }
        observeTasks()
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
            _state.update { current ->
                (current as? State.Loaded)?.let { taskState ->
                    val selected = taskState.selected
                    deleteTasksUseCase(selected.toList())
                    taskState.copy(selected = emptySet())
                } ?: current
            }
        }
    }

    private fun handleSelectTask(taskId: Long, isSelected: Boolean) {
        _state.update { current ->
            (current as? State.Loaded)?.let { taskState ->
                val selectedTasks = taskState.selected.toMutableSet().apply {
                    if (isSelected) {
                        add(taskId)
                    } else {
                        remove(taskId)
                    }
                }

                taskState.copy(selected = selectedTasks)
            } ?: current
        }
    }

    private fun handleSelectionClear() {
        _state.update { current ->
            (current as? State.Loaded)?.copy(selected = emptySet()) ?: current
        }
    }
}
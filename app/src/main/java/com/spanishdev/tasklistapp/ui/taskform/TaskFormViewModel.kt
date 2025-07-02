package com.spanishdev.tasklistapp.ui.taskform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spanishdev.tasklistapp.domain.usecase.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    data class State(
        val name: String = "",
        val description: String = "",
        val isLoading: Boolean = false,
        val error: Error? = null,
    )

    sealed class Error {
        abstract val message: String

        data class InvalidName(override val message: String) : Error()
        data class InvalidDescription(override val message: String) : Error()
        data class GenericError(override val message: String) : Error()
    }

    sealed class Event {
        data class NameChanged(val text: String) : Event()
        data class DescriptionChanged(val text: String) : Event()
        data object CreateTask : Event()
        data object ClearError : Event()
    }

    sealed class NavigationEvent {
        data object TaskAddedSuccessfully : NavigationEvent()
    }

    private val _uiState = MutableStateFlow(State())
    val state: StateFlow<State> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun sendEvent(event: Event) = when (event) {
        is Event.CreateTask -> addTask()
        is Event.ClearError -> _uiState.value = _uiState.value.copy(error = null)

        is Event.DescriptionChanged -> {
            _uiState.value = _uiState.value
                .copy(description = event.text)
                .clearErrorIfType<Error.InvalidDescription>()
                .clearErrorIfType<Error.GenericError>()
        }

        is Event.NameChanged -> {
            _uiState.value = _uiState.value
                .copy(name = event.text)
                .clearErrorIfType<Error.InvalidName>()
                .clearErrorIfType<Error.GenericError>()
        }
    }

    private fun addTask() {
        viewModelScope.launch {
            if (_uiState.value.isLoading) return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                addTaskUseCase(state.value.name, state.value.description)
                finishAndReturn()
            } catch (e: AddTaskUseCase.InvalidTaskNameException) {
                _uiState.value = _uiState.value.copy(error = Error.InvalidName(e.message))
            } catch (e: AddTaskUseCase.InvalidTaskDescriptionException) {
                _uiState.value = _uiState.value.copy(error = Error.InvalidDescription(e.message))
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = Error.GenericError(e.message ?: "Unknown error"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun finishAndReturn() {
        _navigationEvents.emit(NavigationEvent.TaskAddedSuccessfully)
    }

    private inline fun <reified T : Error> State.clearErrorIfType(): State {
        return if (error is T) {
            copy(error = null)
        } else {
            this
        }
    }
}

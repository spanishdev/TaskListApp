package com.spanishdev.tasklistapp.ui.addtask

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spanishdev.tasklistapp.R
import com.spanishdev.tasklistapp.domain.usecase.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    data class State(
        @StringRes val nameLabel: Int = R.string.add_task_name_label,
        @StringRes val nameHint: Int = R.string.add_task_name_hint,
        @StringRes val descriptionLabel: Int = R.string.add_task_description_label,
        @StringRes val descriptionHint: Int = R.string.add_task_description_hint,
        @StringRes val buttonText: Int = R.string.add_task_button,
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(State())
    val state: StateFlow<State> = _uiState.asStateFlow()

    fun addTask(name: String, description: String) {
        viewModelScope.launch {
            if (_uiState.value.isLoading) return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val newTask = addTaskUseCase(name, description)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

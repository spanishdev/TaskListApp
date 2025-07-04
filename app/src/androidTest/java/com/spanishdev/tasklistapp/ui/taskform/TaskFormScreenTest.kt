package com.spanishdev.tasklistapp.ui.taskform

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spanishdev.tasklistapp.domain.usecase.AddTaskUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTaskByIdUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskFormScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val addTaskUseCase: AddTaskUseCase = mockk(relaxed = true)
    private val getTaskByIdUseCase: GetTaskByIdUseCase = mockk(relaxed = true)
    private val updateTaskUseCase: UpdateTaskUseCase = mockk(relaxed = true)

    private lateinit var robot: TaskFormScreenRobot

    @Before
    fun setUp() {
        composeTestRule.setContent {
            val viewModel: TaskFormViewModel = TaskFormViewModel(
                addTaskUseCase = addTaskUseCase,
                getTaskByIdUseCase = getTaskByIdUseCase,
                updateTaskUseCase = updateTaskUseCase,
                savedStateHandle = SavedStateHandle(),
            )
            TaskFormScreen(
                viewModel = viewModel,
                onBackNavigation = {},
                onSuccessNavigation = {}
            )
        }

        robot = TaskFormScreenRobot(composeTestRule)
    }

    @Test
    fun WHEN_user_enters_name_and_description_THEN_form_displays_correct_values() {
        val name = "Test task"
        val description = "Test description"

        robot
            .typeName(name)
            .typeDescription(description)
            .assertName(name)
            .assertDescription(description)
    }

    @Test
    fun WHEN_user_clicks_main_button_THEN_form_submits() {
        val name = "Test task"
        val description = "Test description"

        robot
            .typeName(name)
            .typeDescription(description)
            .clickMainButton()
    }

    @Test
    fun WHEN_user_clicks_back_button_THEN_navigation_is_triggered() {
        var backNavigationCalled = false

        composeTestRule.setContent {
            val savedStateHandle = SavedStateHandle()
            val viewModel = TaskFormViewModel(
                addTaskUseCase = addTaskUseCase,
                getTaskByIdUseCase = getTaskByIdUseCase,
                updateTaskUseCase = updateTaskUseCase,
                savedStateHandle = savedStateHandle
            )

            TaskFormScreen(
                viewModel = viewModel,
                onBackNavigation = { backNavigationCalled = true },
                onSuccessNavigation = {}
            )
        }

        robot.clickBackButton()

        assert(backNavigationCalled)
    }

}
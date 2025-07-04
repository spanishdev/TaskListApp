package com.spanishdev.tasklistapp.ui.taskform

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.usecase.AddTaskUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTaskByIdUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
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
    private val onBackNavigation: () -> Unit = mockk(relaxed = true)
    private val onSuccessNavigation: () -> Unit = mockk(relaxed = true)

    private lateinit var robot: TaskFormScreenRobot

    @Before
    fun setUp() {
        robot = TaskFormScreenRobot(composeTestRule)
    }

    @Test
    fun WHEN_user_enters_name_and_description_THEN_form_displays_correct_values() {
        buildScreen()
        val name = "Test task"
        val description = "Test description"

        robot
            .typeName(name)
            .typeDescription(description)
            .assertName(name)
            .assertDescription(description)
    }

    @Test
    fun WHEN_edit_mode_THEN_form_displays_correct_values() {
        val name = "Test task"
        val description = "Test description"
        coEvery { getTaskByIdUseCase(123L) } returns Task(123L, name, description, Status.Pending, "")

        buildScreen(123L)

        robot
            .assertName(name)
            .assertDescription(description)
    }

    @Test
    fun WHEN_user_clicks_main_button_THEN_form_submits() {
        buildScreen()
        val name = "Test task"
        val description = "Test description"

        robot
            .typeName(name)
            .typeDescription(description)
            .clickMainButton()

        verify { onSuccessNavigation.invoke() }
    }

    @Test
    fun WHEN_user_clicks_done_button_THEN_form_submits() {
        buildScreen()
        val name = "Test task"
        val description = "Test description"

        robot
            .typeName(name)
            .typeDescription(description)
            .clickDoneButton()

        verify { onSuccessNavigation.invoke() }
    }

    @Test
    fun WHEN_user_clicks_back_button_THEN_navigation_is_triggered() {
        buildScreen()
        robot.clickBackButton()

        composeTestRule.waitForIdle()

        verify { onBackNavigation.invoke() }
    }

    private fun buildScreen(taskId: Long? = null) {
        composeTestRule.setContent {
            val viewModel = TaskFormViewModel(
                addTaskUseCase = addTaskUseCase,
                getTaskByIdUseCase = getTaskByIdUseCase,
                updateTaskUseCase = updateTaskUseCase,
                savedStateHandle = SavedStateHandle().apply { set("taskId", taskId) },
            )
            TaskFormScreen(
                viewModel = viewModel,
                onBackNavigation = { onBackNavigation() },
                onSuccessNavigation = { onSuccessNavigation() }
            )
        }
    }

}
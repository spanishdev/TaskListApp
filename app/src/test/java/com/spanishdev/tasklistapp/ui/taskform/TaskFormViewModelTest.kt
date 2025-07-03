package com.spanishdev.tasklistapp.ui.taskform

import androidx.lifecycle.SavedStateHandle
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.usecase.AddTaskUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTaskByIdUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import com.spanishdev.tasklistapp.ui.taskform.TaskFormViewModel.Error
import com.spanishdev.tasklistapp.ui.taskform.TaskFormViewModel.Event
import com.spanishdev.tasklistapp.ui.taskform.TaskFormViewModel.NavigationEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskFormViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    private val mockUpdateTaskUseCase = mockk<UpdateTaskUseCase>(relaxed = true)
    private val mockAddTaskUseCase = mockk<AddTaskUseCase>(relaxed = true)
    private val mockGetTaskByIdUseCase = mockk<GetTaskByIdUseCase>(relaxed = true)
    private val mockSavedStateHandle = mockk<SavedStateHandle>(relaxed = true)

    private lateinit var viewModel: TaskFormViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Add Tasks Tests

    @Test
    fun `GIVEN null taskId WHEN viewmodel started THEN is not in edit mode`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns null

        viewModel = createViewModel()

        assertFalse(viewModel.isInEditMode)
    }

    @Test
    fun `GIVEN initial state WHEN created THEN has empty state`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns null

        viewModel = createViewModel()

        val state = viewModel.state.first()
        assertEquals("", state.name)
        assertEquals("", state.description)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `GIVEN is not in edit mode WHEN SubmitForm Event THEN save task and finish`() = runTest {
        val task = Task(
            id = 123L,
            name = "Name",
            description = "Description",
            status = Status.InProgress,
            createdAt = "",
        )
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns null
        coEvery { mockAddTaskUseCase(task.name, task.description) } returns task

        val navigationEvents = mutableListOf<NavigationEvent>()
        val job = launch {
            viewModel = createViewModel()
            viewModel.navigationEvents.collect { navigationEvents.add(it) }
        }

        advanceUntilIdle()

        viewModel.sendEvent(Event.NameChanged(task.name))
        viewModel.sendEvent(Event.DescriptionChanged(task.description))
        viewModel.sendEvent(Event.SubmitForm)

        advanceUntilIdle()

        coVerify { mockAddTaskUseCase(task.name, task.description) }

        val state = viewModel.state.first()
        assertEquals(task.name, state.name)
        assertEquals(task.description, state.description)
        assertEquals(NavigationEvent.TaskSavedSuccessfully, navigationEvents[0])

        job.cancel()
    }

    // Edit Tasks Tests

    @Test
    fun `GIVEN taskId WHEN viewmodel started THEN isInEditMode`() = runTest {
        val taskId = 123L
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns taskId

        viewModel = createViewModel()

        assertTrue(viewModel.isInEditMode)
    }

    @Test
    fun `GIVEN taskId WHEN viewmodel started THEN has filled state`() = runTest {
        val task = Task(
            id = 123L,
            name = "Name",
            description = "Description",
            status = Status.InProgress,
            createdAt = "",
        )
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns task.id
        coEvery { mockGetTaskByIdUseCase(task.id) } returns task

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(task.name, state.name)
        assertEquals(task.description, state.description)
    }

    @Test
    fun `GIVEN taskId WHEN mockGetTaskByIdUseCase is null THEN goes back`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns 123L
        coEvery { mockGetTaskByIdUseCase(any()) } returns null

        val navigationEvents = mutableListOf<NavigationEvent>()
        val job = launch {
            viewModel = createViewModel()
            viewModel.navigationEvents.collect { navigationEvents.add(it) }
        }

        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(Error.GenericError("Task not found"), state.error)
        assertEquals(NavigationEvent.GoBack, navigationEvents[0])

        job.cancel()
    }

    @Test
    fun `GIVEN taskId WHEN mockGetTaskByIdUseCase throws exception THEN goes back`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns 123L
        coEvery { mockGetTaskByIdUseCase(any()) } throws Exception("Error getting task")

        val navigationEvents = mutableListOf<NavigationEvent>()
        val job = launch {
            viewModel = createViewModel()
            viewModel.navigationEvents.collect { navigationEvents.add(it) }
        }

        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(Error.GenericError("Error getting task"), state.error)
        assertEquals(NavigationEvent.GoBack, navigationEvents[0])

        job.cancel()
    }

    @Test
    fun `GIVEN is in edit mode WHEN SubmitForm Event THEN update task and finish`() = runTest {
        val taskBefore = Task(
            id = 123L,
            name = "Name",
            description = "Description",
            status = Status.InProgress,
            createdAt = "",
        )

        val taskAfter = Task(
            id = 123L,
            name = "Name Updated",
            description = "Description Updated",
            status = Status.InProgress,
            createdAt = "",
        )
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns 123L
        coEvery { mockGetTaskByIdUseCase(123L) } returns taskBefore
        coEvery { mockUpdateTaskUseCase(any()) } returns true

        val navigationEvents = mutableListOf<NavigationEvent>()
        val job = launch {
            viewModel = createViewModel()
            viewModel.navigationEvents.collect { navigationEvents.add(it) }
        }

        advanceUntilIdle()

        viewModel.sendEvent(Event.NameChanged(taskAfter.name))
        viewModel.sendEvent(Event.DescriptionChanged(taskAfter.description))
        viewModel.sendEvent(Event.SubmitForm)

        advanceUntilIdle()

        coVerify { mockUpdateTaskUseCase(taskAfter) }

        val state = viewModel.state.first()
        assertEquals(taskAfter.name, state.name)
        assertEquals(taskAfter.description, state.description)
        assertEquals(NavigationEvent.TaskSavedSuccessfully, navigationEvents[0])

        job.cancel()
    }

    //Errors

    @Test
    fun `GIVEN any error WHEN Event ClearError THEN clears errors`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns null
        coEvery { mockAddTaskUseCase(any(), any()) } throws Exception("Error getting task")

        viewModel = createViewModel()
        viewModel.sendEvent(Event.SubmitForm)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.error != null)

        viewModel.sendEvent(Event.ClearError)

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `GIVEN InvalidName WHEN Name Changed THEN clears errors`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns null
        coEvery { mockAddTaskUseCase(any(), any()) } throws
                AddTaskUseCase.InvalidTaskNameException("Error getting task")

        viewModel = createViewModel()
        viewModel.sendEvent(Event.SubmitForm)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.error != null)

        viewModel.sendEvent(Event.NameChanged("Name"))

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `GIVEN InvalidDescription WHEN Name Changed THEN clears errors`() = runTest {
        coEvery { mockSavedStateHandle.get<Long>("taskId") } returns null
        coEvery { mockAddTaskUseCase(any(), any()) } throws
                AddTaskUseCase.InvalidTaskDescriptionException("Error getting task")

        viewModel = createViewModel()
        viewModel.sendEvent(Event.SubmitForm)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.error != null)

        viewModel.sendEvent(Event.DescriptionChanged("Description"))

        assertNull(viewModel.state.value.error)
    }

    private fun createViewModel() = TaskFormViewModel(
        updateTaskUseCase = mockUpdateTaskUseCase,
        addTaskUseCase = mockAddTaskUseCase,
        getTaskByIdUseCase = mockGetTaskByIdUseCase,
        savedStateHandle = mockSavedStateHandle,
    )
}

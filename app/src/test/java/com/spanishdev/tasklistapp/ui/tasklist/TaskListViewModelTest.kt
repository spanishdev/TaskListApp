package com.spanishdev.tasklistapp.ui.tasklist

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import com.spanishdev.tasklistapp.domain.usecase.DeleteTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.spanishdev.tasklistapp.ui.tasklist.TaskListViewModel.State
import com.spanishdev.tasklistapp.ui.tasklist.TaskListViewModel.Event
import io.mockk.coVerify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    private val mockUpdateTaskUseCase = mockk<UpdateTaskUseCase>(relaxed = true)
    private val mockDeleteTasksUseCase = mockk<DeleteTasksUseCase>(relaxed = true)
    private val mockGetTasksUseCase = mockk<GetTasksUseCase>()

    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `WHEN initialized THEN collects Loading State`() = runTest {
        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(emptyList())

        viewModel = createViewModel()

        val initialState = viewModel.state.first()
        assertEquals(State.Loading, initialState)
    }

    @Test
    fun `WHEN get tasks emitted THEN collects Loaded State with items`() = runTest {
        val tasks = listOf(
            Task(
                id = 1,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = ""
            ),
            Task(
                id = 2,
                name = "Task 2",
                description = "Description 2",
                status = Status.Done,
                createdAt = ""
            ),
        )

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(tasks)

        viewModel = createViewModel()

        val loadedState = viewModel.state.take(2).last()
        assertEquals(loadedState, State.Loaded(tasks, emptySet()))
    }

    @Test
    fun `WHEN get tasks emitted with empty list THEN collects Empty State`() = runTest {
        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(emptyList())

        viewModel = createViewModel()

        val loadedState = viewModel.state.take(2).last()
        assertEquals(loadedState, State.Empty)
    }

    @Test
    fun `WHEN OrderSelected Event THEN changes sortingState`() = runTest {
        val tasksByDate = listOf(
            Task(
                id = 1,
                name = "Task Name",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = "07/06/2025"
            ),
            Task(
                id = 2,
                name = "Another Task",
                description = "Description 2",
                status = Status.Done,
                createdAt = "09/06/2025"
            ),
        )

        val tasksByName = listOf(
            Task(
                id = 2,
                name = "Another Task",
                description = "Description 2",
                status = Status.Done,
                createdAt = "09/06/2025"
            ),
            Task(
                id = 1,
                name = "Task Name",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = "07/06/2025"
            ),
        )

        coEvery { mockGetTasksUseCase(TaskRepository.TaskSort.CREATE_DATE) } returns flowOf(tasksByDate)
        coEvery { mockGetTasksUseCase(TaskRepository.TaskSort.NAME) } returns flowOf(tasksByName)

        viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(TaskRepository.TaskSort.CREATE_DATE, viewModel.sortingState.value)
        val initialState = viewModel.state.value as State.Loaded
        assertEquals(tasksByDate, initialState.tasks)
        assertEquals(tasksByDate[0].name, "Task Name")

        viewModel.sendEvent(Event.OrderSelected(TaskRepository.TaskSort.NAME))
        advanceUntilIdle()

        assertEquals(TaskRepository.TaskSort.NAME, viewModel.sortingState.value)

        val stateAfterSort = viewModel.state.value as State.Loaded
        assertEquals(tasksByName, stateAfterSort.tasks)
        assertEquals(tasksByName[0].name, "Another Task")

        coVerify { mockGetTasksUseCase(TaskRepository.TaskSort.NAME) }
    }

    @Test
    fun `WHEN error THEN collects Error State`() = runTest {
        val msg = "Something went wrong"

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flow {
            throw Exception(msg)
        }

        viewModel = createViewModel()

        val errorState = viewModel.state.take(2).last()
        assertEquals(errorState, State.Error(msg))
    }

    @Test
    fun `WHEN UpdateTask Event THEN updates task`() = runTest {
        val task = Task(
            id = 1,
            name = "Task 1",
            description = "Description 1",
            status = Status.InProgress,
            createdAt = ""
        )

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(emptyList())
        coEvery { mockUpdateTaskUseCase(any()) } returns true

        viewModel = createViewModel()
        viewModel.sendEvent(Event.UpdateTask(task))

        advanceUntilIdle()

        coVerify { mockUpdateTaskUseCase(task) }
    }

    @Test
    fun `WHEN SelectTask Event selects task THEN updates task selection`() = runTest {
        val tasks = listOf(
            Task(
                id = 1,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = ""
            ),
            Task(
                id = 2,
                name = "Task 2",
                description = "Description 2",
                status = Status.InProgress,
                createdAt = ""
            ),
        )

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(tasks)

        viewModel = createViewModel()
        viewModel.state.drop(1).first { it is State.Loaded }

        viewModel.sendEvent(Event.SelectTask(taskId = 1L, selected = true))
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertTrue(currentState is State.Loaded)
        currentState as State.Loaded
        assertEquals(tasks, currentState.tasks)
        assertEquals(setOf(1L), currentState.selected)
    }

    @Test
    fun `WHEN SelectTask Event unselects task THEN updates task selection`() = runTest {
        val tasks = listOf(
            Task(
                id = 1,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = ""
            ),
            Task(
                id = 2,
                name = "Task 2",
                description = "Description 2",
                status = Status.InProgress,
                createdAt = ""
            ),
        )

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(tasks)

        viewModel = createViewModel(State.Loaded(tasks = tasks, selected = setOf(2L)))

        viewModel.sendEvent(Event.SelectTask(2L, false))
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertTrue(currentState is State.Loaded)
        currentState as State.Loaded
        assertEquals(tasks, currentState.tasks)
        assertEquals(emptySet<Long>(), currentState.selected)
    }

    @Test
    fun `WHEN ClearSelection Event THEN removes all tasks from selected set`() = runTest {
        val tasks = listOf(
            Task(
                id = 1,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = ""
            ),
            Task(
                id = 2,
                name = "Task 2",
                description = "Description 2",
                status = Status.InProgress,
                createdAt = ""
            ),
        )

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns flowOf(tasks)

        viewModel = createViewModel(State.Loaded(tasks = tasks, selected = setOf(1L, 2L)))

        viewModel.sendEvent(Event.ClearSelection)
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertTrue(currentState is State.Loaded)
        currentState as State.Loaded
        assertEquals(tasks, currentState.tasks)
        assertEquals(emptySet<Long>(), currentState.selected)
    }

    @Test
    fun `WHEN DeleteSelectedTasks Event THEN deletes all tasks selected`() = runTest {
        val tasksBefore = listOf(
            Task(
                id = 1,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = ""
            ),
            Task(
                id = 2,
                name = "Task 2",
                description = "Description 2",
                status = Status.InProgress,
                createdAt = ""
            ),
        )

        val tasksAfter = listOf(
            Task(
                id = 1,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = ""
            )
        )

        val tasksFlow = MutableSharedFlow<List<Task>>(replay = 1)
        launch { tasksFlow.emit(tasksBefore) }

        coEvery { mockGetTasksUseCase(DEFAULT_SORT) } returns tasksFlow
        coEvery { mockDeleteTasksUseCase(any()) } returns true

        viewModel = createViewModel(State.Loaded(tasks = tasksBefore, selected = setOf(2L)))

        viewModel.sendEvent(Event.DeleteSelectedTasks)

        //Simulates delete
        launch { tasksFlow.emit(tasksAfter) }

        advanceUntilIdle()

        coVerify { mockDeleteTasksUseCase(listOf(2L)) }

        val currentState = viewModel.state.value
        assertTrue(currentState is State.Loaded)
        currentState as State.Loaded
        assertEquals(tasksAfter, currentState.tasks)
        assertEquals(emptySet<Long>(), currentState.selected)
    }


    private fun createViewModel(initialState: State = State.Loading) = TaskListViewModel(
        updateTaskUseCase = mockUpdateTaskUseCase,
        deleteTasksUseCase = mockDeleteTasksUseCase,
        getTasksUseCase = mockGetTasksUseCase,
        initialState = initialState,
    )

    companion object {
        val DEFAULT_SORT = TaskRepository.TaskSort.CREATE_DATE
    }
}

package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTasksUseCaseTest {

    @Test
    fun `WHEN empty THEN returns empty list`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = GetTasksUseCase(repository, UnconfinedTestDispatcher())

        coEvery { repository.getTasks() } returns emptyList()

        val result = useCase()

        assertEquals(emptyList<Task>(), result)
        coVerify { repository.getTasks() }
    }

    @Test
    fun `WHEN getTasks THEN return task list`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = GetTasksUseCase(repository, UnconfinedTestDispatcher())
        val expectedTasks = listOf(
            Task(
                id = 1,
                name = "Test task 1",
                description = "Description 1",
                status = Status.Pending
            ),
            Task(
                id = 2,
                name = "Test task 2",
                description = "Description 2",
                status = Status.InProgress
            ),
            Task(
                id = 3,
                name = "Test task 3",
                description = "Description 3",
                status = Status.Done
            ),
        )

        coEvery { repository.getTasks() } returns expectedTasks

        val result = useCase()

        assertEquals(expectedTasks, result)
        coVerify { repository.getTasks() }
    }

    @Test
    fun `WHEN error thrown THEN raise an exception`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = GetTasksUseCase(repository, UnconfinedTestDispatcher())
        val expectedException = RuntimeException("Database error")

        coEvery { repository.getTasks() } throws expectedException

        try {
            useCase()
        } catch (e: RuntimeException) {
            assertEquals(expectedException.message, e.message)
        }

        coVerify { repository.getTasks() }
    }
}
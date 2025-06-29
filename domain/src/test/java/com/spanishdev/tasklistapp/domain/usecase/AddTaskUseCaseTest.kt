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
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTaskUseCaseTest {

    @Test
    fun `WHEN addTask THEN returns task id`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = AddTaskUseCase(repository, UnconfinedTestDispatcher())
        val expectedTask = Task(45L, "Name", "Description", Status.Pending)

        coEvery { repository.addTask(any()) } returns 45L

        val task = useCase("Name", "Description")

        assertEquals(expectedTask, task)
        coVerify { repository.addTask( Task(0L, "Name", "Description", Status.Pending)) }
    }

    @Test
    fun `WHEN texts with spaces THEN trims`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = AddTaskUseCase(repository, UnconfinedTestDispatcher())
        val expectedTask = Task(45L, "Name", "Description", Status.Pending)

        coEvery { repository.addTask(any()) } returns 45L

        val task = useCase("     Name     ", "    Description    ")

        assertEquals(expectedTask, task)
        coVerify { repository.addTask( Task(0L, "Name", "Description", Status.Pending)) }
    }

    @Test
    fun `WHEN blank name THEN throws error`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = AddTaskUseCase(repository, UnconfinedTestDispatcher())

        coEvery { repository.addTask(any()) } returns 45L

        try {
            useCase("", "Description")
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Task name cannot be blank", e.message)
        }

        coVerify(exactly = 0) { repository.addTask(any()) }
    }

    @Test
    fun `WHEN error thrown THEN raise an exception`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = AddTaskUseCase(repository, UnconfinedTestDispatcher())
        val expectedException = RuntimeException("Database error")

        coEvery { repository.addTask(any()) } throws expectedException

        try {
            useCase("Name", "Description")
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(expectedException.message, e.message)
        }

        coVerify { repository.addTask(Task(0L, "Name", "Description", Status.Pending)) }
    }
}
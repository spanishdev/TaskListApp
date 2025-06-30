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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateTaskUseCaseTest {

    @Test
    fun `WHEN Update Task success THEN returns true`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = UpdateTaskUseCase(repository, UnconfinedTestDispatcher())
        val taskToDelete = Task(45L, "Name", "Description", Status.Pending, DEFAULT_DATE)

        coEvery { repository.updateTask(any()) } returns true

        val task = useCase(taskToDelete)

        assertTrue(task)
        coVerify { repository.updateTask(taskToDelete) }
    }

    @Test
    fun `WHEN Update Task fail THEN returns false`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = UpdateTaskUseCase(repository, UnconfinedTestDispatcher())
        val taskToDelete = Task(45L, "Name", "Description", Status.Pending, DEFAULT_DATE)

        coEvery { repository.updateTask(any()) } returns false

        val task = useCase(taskToDelete)

        assertFalse(task)
        coVerify { repository.updateTask(taskToDelete) }
    }

    @Test
    fun `WHEN error thrown THEN raise an exception`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = UpdateTaskUseCase(repository, UnconfinedTestDispatcher())
        val expectedException = RuntimeException("Database error")
        val taskToDelete = Task(45L, "Name", "Description", Status.Pending, DEFAULT_DATE)

        coEvery { repository.updateTask(any()) } throws expectedException

        try {
            useCase(taskToDelete)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(expectedException.message, e.message)
        }

        coVerify { repository.updateTask(any()) }
    }

    companion object {
        const val DEFAULT_DATE = "12-06-2025 14:23"
    }
}
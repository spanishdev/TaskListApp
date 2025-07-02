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
class DeleteTasksUseCaseTest {

    @Test
    fun `WHEN Delete Task success THEN returns true`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = DeleteTasksUseCase(repository, UnconfinedTestDispatcher())
        val taskToDelete = Task(45L, "Name", "Description", Status.Pending, DEFAULT_DATE)

        coEvery { repository.deleteTasks(any()) } returns true

        val task = useCase(listOf(taskToDelete.id))

        assertTrue(task)
        coVerify { repository.deleteTasks(listOf(taskToDelete.id)) }
    }

    @Test
    fun `WHEN Delete Task fail THEN returns false`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = DeleteTasksUseCase(repository, UnconfinedTestDispatcher())
        val taskToDelete = Task(45L, "Name", "Description", Status.Pending, DEFAULT_DATE)

        coEvery { repository.deleteTasks(any()) } returns false

        val task = useCase(listOf(taskToDelete.id))

        assertFalse(task)
        coVerify { repository.deleteTasks(listOf(taskToDelete.id)) }
    }

    @Test
    fun `WHEN error thrown THEN raise an exception`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = DeleteTasksUseCase(repository, UnconfinedTestDispatcher())
        val expectedException = RuntimeException("Database error")
        val taskToDelete = Task(45L, "Name", "Description", Status.Pending, DEFAULT_DATE)

        coEvery { repository.deleteTasks(any()) } throws expectedException

        try {
            useCase(listOf(taskToDelete.id))
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(expectedException.message, e.message)
        }

        coVerify { repository.deleteTasks(any()) }
    }

    companion object {
        const val DEFAULT_DATE = "12-06-2025 14:23"
    }
}
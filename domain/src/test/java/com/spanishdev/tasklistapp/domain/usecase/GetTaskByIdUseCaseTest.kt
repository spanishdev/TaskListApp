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
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTaskByIdUseCaseTest {

    @Test
    fun `WHEN non-exist THEN returns null`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = GetTaskByIdUseCase(repository, UnconfinedTestDispatcher())
        coEvery { repository.getTaskById(DEFAULT_ID) } returns null

        val result = useCase(DEFAULT_ID)

        assertNull(result)
        coVerify { repository.getTaskById(DEFAULT_ID) }
    }

    @Test
    fun `WHEN exist THEN returns task`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = GetTaskByIdUseCase(repository, UnconfinedTestDispatcher())
        val expectedTask = Task(
            id = 1,
            name = "Test task 1",
            description = "Description 1",
            status = Status.Pending,
            createdAt = DEFAULT_DATE
        )
        coEvery { repository.getTaskById(DEFAULT_ID) } returns expectedTask

        val result = useCase(DEFAULT_ID)

        assertEquals(expectedTask, result)
        coVerify { repository.getTaskById(DEFAULT_ID) }
    }

    @Test
    fun `WHEN error thrown THEN raise an exception`() = runTest {
        val repository = mockk<TaskRepository>()
        val useCase = GetTaskByIdUseCase(repository, UnconfinedTestDispatcher())
        val expectedException = RuntimeException("Database error")

        coEvery { repository.getTaskById(DEFAULT_ID) } throws expectedException

        try {
            useCase(DEFAULT_ID)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(expectedException.message, e.message)
        }

        coVerify { repository.getTaskById(DEFAULT_ID) }
    }

    companion object {
        const val DEFAULT_ID = 1L
        const val DEFAULT_DATE = "12-06-2025 14:23"
    }
}
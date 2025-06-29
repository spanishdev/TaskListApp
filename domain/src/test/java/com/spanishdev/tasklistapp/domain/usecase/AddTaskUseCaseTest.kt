package com.spanishdev.tasklistapp.domain.usecase

import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import net.bytebuddy.matcher.ElementMatchers.any
import org.junit.Assert.assertEquals
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
        coVerify { repository.addTask(task.copy(id = 0L)) }
    }
}
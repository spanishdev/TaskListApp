package com.spanishdev.tasklistapp.data

import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.database.entities.TaskEntity
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskRepositoryImplTest {

    private val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    private val taskDao: TaskDao = mockk()
    private val taskMapper = TaskMapper(formatter)
    private val taskRepository = TaskRepositoryImpl(taskDao, taskMapper)

    @Test
    fun `WHEN add task THEN inserts Task Entity`() = runTest {
        val task = Task(
            id = 1L,
            name = "Name",
            description = "Description",
            status = Status.InProgress,
            createdAt = "12-06-2025 13:00"
        )

        coEvery { taskDao.insertTask(any()) } returns 1L

        taskRepository.addTask(task)

        coVerify {
            taskDao.insertTask(
                match { entity ->
                    entity.id == 1L
                            && entity.name == "Name"
                            && entity.description == "Description"
                            && entity.status == "IN_PROGRESS"
                }
            )
        }
    }

    @Test
    fun `WHEN delete task and rowsaffected gt 0 THEN returns true`() = runTest {
        val taskList = listOf(1L)

        coEvery { taskDao.deleteTasksByIds(any()) } returns 1

        val deleted = taskRepository.deleteTasks(taskList)

        assertTrue(deleted)
        coVerify {
            taskDao.deleteTasksByIds(taskList)
        }
    }

    @Test
    fun `WHEN delete task and rowsaffected is 0 THEN returns false`() = runTest {
        val taskList = listOf(1L)

        coEvery { taskDao.deleteTasksByIds(any()) } returns 0

        val deleted = taskRepository.deleteTasks(taskList)

        assertFalse(deleted)
        coVerify {
            taskDao.deleteTasksByIds(taskList)
        }
    }

    @Test
    fun `WHEN update task and rowsaffected gt 0 THEN returns true`() = runTest {
        val task = Task(
            id = 1L,
            name = "Name",
            description = "Description",
            status = Status.InProgress,
            createdAt = "12-06-2025 13:00"
        )

        coEvery { taskDao.updateTask(any()) } returns 1

        val updated = taskRepository.updateTask(task)

        assertTrue(updated)
        coVerify {
            taskDao.updateTask(
                match { entity ->
                    entity.id == 1L
                            && entity.name == "Name"
                            && entity.description == "Description"
                            && entity.status == "IN_PROGRESS"
                }
            )
        }
    }

    @Test
    fun `WHEN update task and rowsaffected is 0 THEN returns false`() = runTest {
        val task = Task(
            id = 1L,
            name = "Name",
            description = "Description",
            status = Status.InProgress,
            createdAt = "12-06-2025 13:00"
        )

        coEvery { taskDao.updateTask(any()) } returns 0

        val updated = taskRepository.updateTask(task)

        assertFalse(updated)
        coVerify {
            taskDao.updateTask(
                match { entity ->
                    entity.id == 1L
                            && entity.name == "Name"
                            && entity.description == "Description"
                            && entity.status == "IN_PROGRESS"
                }
            )
        }
    }

    @Test
    fun `WHEN get tasks THEN returns task list`() = runTest {
        val taskList = listOf(
            TaskEntity(
                id = 1L,
                name = "Task 1",
                description = "Description 1",
                status = "IN_PROGRESS",
                createdAt = 1719752400000L
            ),
            TaskEntity(
                id = 2L,
                name = "Task 2",
                description = "Description 2",
                status = "DONE",
                createdAt = 1719756000000L
            ),
        )

        coEvery { taskDao.getAllTasks() } returns flowOf(taskList)

        val list = taskRepository.getTasks().first()

        val expected = listOf(
            Task(
                id = 1L,
                name = "Task 1",
                description = "Description 1",
                status = Status.InProgress,
                createdAt = formatter.format(Date(1719752400000L))
            ),
            Task(
                id = 2L,
                name = "Task 2",
                description = "Description 2",
                status = Status.Done,
                createdAt = formatter.format(Date(1719756000000L))
            ),
        )

        assertTrue(list == expected)

        coVerify {
            taskDao.getAllTasks()
        }
    }

    @Test
    fun `WHEN getTaskById finds task THEN returns task`() = runTest {
        val taskEntity = TaskEntity(
            id = 1L,
            name = "Task 1",
            description = "Description 1",
            status = "IN_PROGRESS",
            createdAt = 1719756000000L
        )

        coEvery { taskDao.getTaskById(1L) } returns taskEntity

        val task = taskRepository.getTaskById(1L)

        val expected = Task(
            id = 1L,
            name = "Task 1",
            description = "Description 1",
            status = Status.InProgress,
            createdAt = formatter.format(1719756000000L)
        )

        assertTrue(task == expected)

        coVerify {
            taskDao.getTaskById(1L)
        }
    }

    @Test
    fun `WHEN getTaskById doesnt find task THEN returns null`() = runTest {
        coEvery { taskDao.getTaskById(any()) } returns null

        val task = taskRepository.getTaskById(1L)

        assertNull(task)

        coVerify {
            taskDao.getTaskById(1L)
        }
    }
}

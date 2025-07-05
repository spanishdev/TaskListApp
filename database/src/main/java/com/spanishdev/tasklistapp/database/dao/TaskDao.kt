package com.spanishdev.tasklistapp.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.spanishdev.tasklistapp.database.entities.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt ASC")
    fun getAllTasksByCreatedAtAsc(): PagingSource<Int, TaskEntity>

    @Query("SELECT * FROM tasks ORDER BY name ASC")
    fun getAllTasksByName(): PagingSource<Int, TaskEntity>

    @Query(
        """
        SELECT * FROM tasks 
        ORDER BY
                 CASE status
                    WHEN 'PENDING' THEN 1
                    WHEN 'IN_PROGRESS' THEN 2  
                    WHEN 'DONE' THEN 3
                    WHEN 'CANCELLED' THEN 4
                    ELSE 5
                END ASC, 
                createdAt DESC
    """
    )
    fun getAllTasksByStatus(): PagingSource<Int, TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert
    suspend fun insertTask(taskEntity: TaskEntity): Long

    @Update
    suspend fun updateTask(taskEntity: TaskEntity): Int

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    suspend fun deleteTasksByIds(taskIds: List<Long>): Int
}
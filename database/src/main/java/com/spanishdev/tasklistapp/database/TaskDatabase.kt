package com.spanishdev.tasklistapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spanishdev.tasklistapp.database.TaskDatabase.Companion.DATABASE_VERSION
import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.database.entities.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false,
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "task_database"
        const val DATABASE_VERSION = 1
    }
}
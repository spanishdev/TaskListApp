package com.spanishdev.tasklistapp.database.di

import android.content.Context
import androidx.room.Room
import com.spanishdev.tasklistapp.database.TaskDatabase
import com.spanishdev.tasklistapp.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            TaskDatabase::class.java,
            TaskDatabase.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase): TaskDao =
        database.taskDao()
}
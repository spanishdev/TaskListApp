package com.spanishdev.tasklistapp.di

import com.spanishdev.tasklistapp.data.TaskMapper
import com.spanishdev.tasklistapp.data.TaskRepositoryImpl
import com.spanishdev.tasklistapp.database.dao.TaskDao
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDateFormat(): DateFormat = SimpleDateFormat(
        "dd-MM-yyyy HH:mm",
        Locale.getDefault()
    )

    @Provides
    @Singleton
    fun provideTaskMapper(dateFormat: DateFormat): TaskMapper = TaskMapper(
        dateFormat = dateFormat
    )

    @Provides
    @Singleton
    fun provideRepository(dao: TaskDao, taskMapper: TaskMapper): TaskRepository =
        TaskRepositoryImpl(
            taskDao = dao,
            taskMapper = taskMapper,
        )
}
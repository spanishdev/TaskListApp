package com.spanishdev.tasklistapp.di

import androidx.lifecycle.ViewModelProvider
import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

@Module
@InstallIn(ViewModelProvider::class)
object UseCaseModule {

    @Provides
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase =
        GetTasksUseCase(repository)
}

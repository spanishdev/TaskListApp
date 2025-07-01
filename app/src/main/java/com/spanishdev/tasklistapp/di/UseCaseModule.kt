package com.spanishdev.tasklistapp.di

import com.spanishdev.tasklistapp.domain.repository.TaskRepository
import com.spanishdev.tasklistapp.domain.usecase.AddTaskUseCase
import com.spanishdev.tasklistapp.domain.usecase.GetTasksUseCase
import com.spanishdev.tasklistapp.domain.usecase.UpdateTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase =
        GetTasksUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideAddTaskUseCase(repository: TaskRepository): AddTaskUseCase =
        AddTaskUseCase(repository)


    @Provides
    @ViewModelScoped
    fun provideUpdateTaskUseCase(repository: TaskRepository): UpdateTaskUseCase =
        UpdateTaskUseCase(repository)
}

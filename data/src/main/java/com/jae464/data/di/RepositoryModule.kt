package com.jae464.data.di

import com.jae464.data.repository.CategoryRepositoryImpl
import com.jae464.data.repository.ProgressTaskRepositoryImpl
import com.jae464.data.repository.TaskRepositoryImpl
import com.jae464.domain.repository.CategoryRepository
import com.jae464.domain.repository.ProgressTaskRepository
import com.jae464.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    fun bindProgressTaskRepository(
        progressTaskRepositoryImpl: ProgressTaskRepositoryImpl
    ): ProgressTaskRepository
}
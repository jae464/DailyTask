package com.jae464.data.di

import com.jae464.data.database.DailyTaskDataBase
import com.jae464.data.database.dao.CategoryDao
import com.jae464.data.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun provideTaskDao(
        database: DailyTaskDataBase
    ): TaskDao = database.taskDao()

    @Provides
    fun provideCategoryDao(
        database: DailyTaskDataBase
    ): CategoryDao = database.categoryDao()
}

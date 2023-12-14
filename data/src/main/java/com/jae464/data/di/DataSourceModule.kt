package com.jae464.data.di

import com.jae464.data.datasource.CategoryLocalDataSource
import com.jae464.data.datasource.CategoryLocalDataSourceImpl
import com.jae464.data.datasource.ProgressTaskLocalDataSource
import com.jae464.data.datasource.ProgressTaskLocalDataSourceImpl
import com.jae464.data.datasource.TaskLocalDataSource
import com.jae464.data.datasource.TaskLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    @Singleton
    fun bindTaskLocalDataSource(
        source: TaskLocalDataSourceImpl
    ): TaskLocalDataSource

    @Binds
    @Singleton
    fun bindCategoryLocalDataSource(
        source: CategoryLocalDataSourceImpl
    ): CategoryLocalDataSource

    @Binds
    @Singleton
    fun bindProgressTaskLocalDataSource(
        source: ProgressTaskLocalDataSourceImpl
    ): ProgressTaskLocalDataSource
}
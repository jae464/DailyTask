package com.jae464.data.di

import android.content.Context
import androidx.room.Room
import com.jae464.data.database.DailyTaskDataBase
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
    fun providesDailyTaskDatabase(
        @ApplicationContext context: Context
    ): DailyTaskDataBase = Room.databaseBuilder(
        context,
        DailyTaskDataBase::class.java,
        "daily-task-database"
    )
        .addCallback(callback = DailyTaskDataBase.callback)
        .build()
}
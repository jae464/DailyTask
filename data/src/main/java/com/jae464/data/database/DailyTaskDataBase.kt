package com.jae464.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jae464.data.database.dao.CategoryDao
import com.jae464.data.database.dao.TaskDao
import com.jae464.data.database.entity.CategoryEntity
import com.jae464.data.database.entity.TaskEntity
import com.jae464.data.database.util.DayOfWeekConverter
import com.jae464.data.database.util.HourMinuteConverter
import com.jae464.data.database.util.LocalDateTimeConverter
import com.jae464.data.database.util.TaskTypeConverter

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class
    ],
    version = 1
)
@TypeConverters(
    HourMinuteConverter::class,
    TaskTypeConverter::class,
    DayOfWeekConverter::class,
    LocalDateTimeConverter::class
)
abstract class DailyTaskDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}
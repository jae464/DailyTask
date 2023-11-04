package com.jae464.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jae464.data.database.dao.CategoryDao
import com.jae464.data.database.dao.ProgressTaskDao
import com.jae464.data.database.dao.TaskDao
import com.jae464.data.database.entity.CategoryEntity
import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.TaskEntity
import com.jae464.data.database.util.DayOfWeekConverter
import com.jae464.data.database.util.LocalDateConverter
import com.jae464.data.database.util.LocalDateTimeConverter
import com.jae464.data.database.util.TaskTypeConverter
import java.util.concurrent.Executors

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
        ProgressTaskEntity::class
    ],
    version = 1
)
@TypeConverters(
    TaskTypeConverter::class,
    DayOfWeekConverter::class,
    LocalDateTimeConverter::class,
    LocalDateConverter::class
)
abstract class DailyTaskDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun progressTaskDao(): ProgressTaskDao

    companion object {
        val callback = object: Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    db.execSQL("INSERT INTO categories (category_name) VALUES ('기타')")
                    db.execSQL("INSERT INTO categories (category_name) VALUES ('학습')")
                }
            }
        }
    }
}
package com.jae464.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jae464.data.database.DailyTaskDataBase
import com.jae464.data.database.entity.CategoryEntity
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime
import com.jae464.data.database.entity.TaskEntity as TaskEntity

class TaskDaoTest {
    private lateinit var taskDao: TaskDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var db: DailyTaskDataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            DailyTaskDataBase::class.java
        ).build()
        taskDao = db.taskDao()
        categoryDao = db.categoryDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun taskDao_insert_task() = runTest {
        // given
        categoryDao.insertCategory(categoryEntities[0])

        val categories = categoryDao.getAllCategories().first()
        println(categories)

        taskDao.insertTask(taskEntities[0])

        // when
        val tasks = taskDao.getAllTasks().first()

        println(tasks)

        // then
        assertEquals(
            "test1",
            tasks[0].id
        )



    }

    companion object {
        private val categoryEntities = listOf(
            CategoryEntity(
                0L, "학습"
            ),
            CategoryEntity(
                1L, "운동"
            )
        )

        private val taskEntities = listOf(
            TaskEntity(
                id = "test1",
                title = "안드로이드 개인 프로젝트",
                progressTime = HourMinute(hour = 3,minute = 0),
                taskType = TaskType.Regular,
                dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                categoryId = 1L,
                alarmTime = LocalDateTime.now(),
                createdAt = LocalDateTime.now()
            ),
            TaskEntity(
                id = "test2",
                title = "팔굽혀펴기 30회",
                progressTime = HourMinute(1,0),
                taskType = TaskType.Irregular,
                dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                categoryId = 1L,
                alarmTime = LocalDateTime.now(),
                createdAt = LocalDateTime.now()
            )
        )
    }


}
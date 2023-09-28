package com.jae464.data.database.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jae464.data.database.entity.TaskEntity

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Query(
        value = """
            DELETE FROM tasks
            WHERE id = :taskId
        """
    )
    suspend fun deleteTask(taskId: String)
}
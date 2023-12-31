package com.example.homesweethome

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CleaningTaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(cleaningTask: CleaningTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cleaningTasks: List<CleaningTask>)

    @Query("SELECT * FROM cleaning_tasks WHERE assignedDate = :date")
    fun getTasksByDate(date: String): Flow<List<CleaningTask>>

    @Query("SELECT * FROM cleaning_tasks")
    fun getAllTasks(): Flow<List<CleaningTask>>

    @Query("SELECT COUNT(*) FROM cleaning_tasks")
    fun countAllTasks(): Long

    @Query("SELECT * FROM cleaning_tasks WHERE isCompleted = 1")
    fun getCompletedTasks(): Flow<List<CleaningTask>>

    @Query("SELECT COUNT(*) FROM cleaning_tasks WHERE isCompleted = 1")
    fun countCompletedTasks(): Long

    @Query("SELECT * FROM cleaning_tasks WHERE isCompleted = 0")
    fun getIncompleteTasks(): Flow<List<CleaningTask>>

    @Update
    fun updateTask(cleaningTask: CleaningTask)

    @Query("SELECT * FROM cleaning_tasks WHERE taskId = :taskId")
    fun getTaskById(taskId: Long): CleaningTask

    @Query("DELETE FROM cleaning_tasks")
    fun deleteAllTasks()

}


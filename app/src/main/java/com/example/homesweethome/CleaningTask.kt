package com.example.homesweethome

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleaning_tasks")
data class CleaningTask(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val taskName: String,
    val taskType: String,
    val assignedDate: String,
    var isCompleted: Boolean = false
)

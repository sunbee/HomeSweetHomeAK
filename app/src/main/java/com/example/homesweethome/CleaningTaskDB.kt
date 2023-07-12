package com.example.homesweethome

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CleaningTask::class], version = 1)
abstract class CleaningTaskDB : RoomDatabase() {
    abstract fun CleaningTaskDao() : CleaningTaskDao
}

/*
package com.example.pong

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MyScore::class], version = 1)
abstract class MyScoreDB : RoomDatabase() {
    abstract fun MyScoreDao(): MyScoreDao
}
* */
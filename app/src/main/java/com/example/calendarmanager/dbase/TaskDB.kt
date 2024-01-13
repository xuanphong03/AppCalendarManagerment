package com.example.calendarmanager.dbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calendarmanager.models.dto.Task

@Database(entities = arrayOf(Task::class), version = 1, exportSchema = false)
abstract class TaskDB: RoomDatabase(){
    abstract fun getTaskDao(): TaskDao
    companion object{
        @Volatile
        private var INSTANCE: TaskDB? = null

        fun getDB(context: Context): TaskDB{
            return  INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDB::class.java,
                    "dbdemo"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}



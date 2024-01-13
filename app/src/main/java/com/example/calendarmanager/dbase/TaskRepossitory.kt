package com.example.calendarmanager.dbase

import androidx.lifecycle.LiveData
import com.example.calendarmanager.models.dto.Task

class TaskRepossitory(private val noteDao: TaskDao) {
    val allTask: LiveData<List<Task>> = noteDao.getAllTask()
    suspend fun insert(task: Task){
        noteDao.insert(task)
    }
    suspend fun delete(task: Task){
        noteDao.delete(task)
    }
    suspend fun update(task: Task){
        noteDao.update(task.id,task.title,task.content,task.date,task.isDone)
    }

}


package com.example.calendarmanager.dbase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.calendarmanager.models.dto.Task
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.io.Serializable

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)
    @Delete
    suspend fun delete(task: Task)
    @Query("SELECT * FROM Task_tb ORDER BY date DESC")
    fun getAllTask(): LiveData<List<Task>>
    @Query("UPDATE Task_tb SET title = :title, content = :content, date = :date, isDone = :isDone WHERE id = :id")
    suspend fun update(id: Int?, title: String?,content: String?,date: String?,isDone: Boolean?)
}

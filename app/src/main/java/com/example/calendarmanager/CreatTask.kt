package com.example.calendarmanager

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.calendarmanager.databinding.ActivityCreatTaskBinding
import com.example.calendarmanager.models.const
import com.example.calendarmanager.models.dto.Task
import java.text.SimpleDateFormat
import java.util.*

class CreatTask : AppCompatActivity() {
    private lateinit var binding: ActivityCreatTaskBinding
    private lateinit var task: Task
    private lateinit var old_task: Task
    private lateinit var date: String
    var isUpdate = false
    var isDone_rn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        date = intent.getSerializableExtra("date") as String
        binding.editnothingdate.setText(date)
        try {
            old_task = intent.getSerializableExtra("current_task") as Task
            binding.edtTitle.setText(old_task.title)
            binding.edtContent.setText(old_task.content)
            if(old_task.isDone == true){
                binding.cbIsdone.setChecked(true);
            }
            isUpdate = true
        }catch (exception: Exception){
            exception.printStackTrace()
        }
        binding.btnDone.setOnClickListener {
            val title_str = binding.edtTitle.text.toString()
            val note_str = binding.edtContent.text.toString()
            if (binding.cbIsdone.isChecked){
                isDone_rn = true
            }
            if(title_str.isNotEmpty() || note_str.isNotEmpty()){
                if(isUpdate){
                    task = Task(old_task.id,title_str,note_str,date,isDone_rn)
                }else{
                    task = Task(null,title_str,note_str,date,isDone_rn)
                }
                val intent = Intent()
                intent.putExtra("task",task)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }else{
                Toast.makeText(this@CreatTask, "Please Typing some data !!!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
        binding.btnExit.setOnClickListener {
            onBackPressed()
        }
        binding.txtRandomNote.setText(const.createNote())
    }
}
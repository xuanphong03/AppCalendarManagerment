package com.example.calendarmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.calendarmanager.adapter.TaskAdapter
import com.example.calendarmanager.databinding.ActivityMainBinding
import com.example.calendarmanager.dbase.TaskDB
import com.example.calendarmanager.models.TaskViewModel
import com.example.calendarmanager.models.dto.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity(),TaskAdapter.TaskOnClickListener {
    private var datetoString: String = ""
    private lateinit var dbase: TaskDB
    lateinit var viewModel: TaskViewModel
    lateinit var adapter: TaskAdapter

    /*
        registerForActivityResult -  sử dụng để nhận kết quả từ các hoạt động
        nhận ActivityResultContracts.StartActivityForResult() là tham số
    */
    private  val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val note = result.data?.getSerializableExtra("task") as? Task
            if(note != null){
                viewModel.updateTask(note)
                setColorDate()
            }
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCalendarDisplayModeWeeks()

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setCalendarDisplayModeMonths()
                setColorDate()
            }else{
                setCalendarDisplayModeWeeks()
                setColorDate()
            }

        }

        binding.calendarView.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                datetoString = date.toString()
                Toast.makeText(this@MainActivity, datetoString, Toast.LENGTH_SHORT).show()
                // truy xuất SQL khi click calendarView
                adapter.searchFilter(datetoString)
            }
        })

        f_init()

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(TaskViewModel::class.java)
        viewModel.allTask.observe(this){list->
            list?.let{
                adapter.updateList(list)
                setColorDate()
            }
        }
        dbase = TaskDB.getDB(this)

        setColorDate()
    }

    private fun setCalendarDisplayModeWeeks() {
        binding.calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.WEEKS)
            .setFirstDayOfWeek(Calendar.MONDAY)
            .commit()
        setColorDate()
    }

    private fun setCalendarDisplayModeMonths() {
        binding.calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .setFirstDayOfWeek(Calendar.MONDAY)
            .commit()
        setColorDate()
    }

    private fun setColorDate() {
        val colorTaskPending = resources.getColor(R.color.do_12)
        val colorTaskCompleted = resources.getColor(R.color.do_1)
        val colorToday = resources.getColor(R.color.black)
        val colorPast = resources.getColor(R.color.do_24)
        val colorFuture = resources.getColor(R.color.do_11)

        // Thiết lập màu sắc cho ngày hôm nay
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day == CalendarDay.today()
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(colorToday))
                view.addSpan(StyleSpan(Typeface.BOLD))
            }
        })

        // Thiết lập màu sắc cho ngày quá khứ
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day.isBefore(CalendarDay.today())
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(colorPast))
            }
        })

        // Thiết lập màu sắc cho ngày tương lai
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day.isAfter(CalendarDay.today())
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(colorFuture))
            }
        })

        // Thiết lập màu sắc cho ngày chưa hoàn thành
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val tasks = getTasksForDate(day)
                return tasks.any { !it.isDone }
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(colorTaskPending))
            }
        })
        // Thiết lập màu sắc cho ngày đã hoàn thành
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val tasks = getTasksForDate(day)
                return tasks.isNotEmpty() && tasks.all { it.isDone }
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(colorTaskCompleted))
            }
        })
    }
    private fun f_init() {
        binding.rview.setHasFixedSize(true)
        binding.rview.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        adapter = TaskAdapter(this,this)
        binding.rview.adapter = adapter
        val getContext = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            val note = result.data?.getSerializableExtra("task") as? Task
            if(note != null){
                viewModel.insert(note)
                setColorDate()
            }
        }
        binding.btnCreateTask.setOnClickListener {
            val intent = Intent(this, CreatTask::class.java)
            intent.putExtra("date",datetoString)
            getContext.launch(intent)

        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText !=null){
                    adapter.searchFilter(newText)
                }
                return true
            }
        })
    }
    private fun getTasksForDate(date: CalendarDay): ArrayList<Task> {
        val tasksForDate = ArrayList<Task>()
        for (task in adapter.dayofTasks()) {
            if (task.date == date.toString()) {
                tasksForDate.add(task)
            }
        }
        return tasksForDate
    }

    override fun onItemClick(task: Task) {
        val intent = Intent(this@MainActivity, CreatTask::class.java)
        intent.putExtra("current_task",task)
        intent.putExtra("date",datetoString)
        updateNote.launch(intent)
    }

    override fun onDeleteItemClick(task: Task) {
        viewModel.deleteTask(task)
    }
}

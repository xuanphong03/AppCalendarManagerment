package com.example.calendarmanager.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calendarmanager.R
import com.example.calendarmanager.models.const
import com.example.calendarmanager.models.dto.Task
import kotlin.random.Random

class TaskAdapter(private val context: Context, val ltn: TaskOnClickListener):
    RecyclerView.Adapter<TaskAdapter.NoteViewHolder>() {

    private val noteList = ArrayList<Task>()
    private val fullList = ArrayList<Task>()

    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.each_task,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.itemView.apply {
            val colorLayout = findViewById<CardView>(R.id.card_layout)
            val image = findViewById<ImageView>(R.id.imgNote)
            val title = findViewById<TextView>(R.id.txtTitle)
            val note = findViewById<TextView>(R.id.txtNote)
            val date = findViewById<TextView>(R.id.txtDate)
            val Isdone = findViewById<TextView>(R.id.txtisDone)
            val btn_delete = findViewById<ImageButton>(R.id.btn_delete)
            val index= random()
            const.create()

            title.text = noteList[position].title
            title.isSelected = true
            note.text = noteList[position].content
            date.text = noteList[position].date
            date.isSelected = true
            Isdone.text = "IsDone: ${noteList[position].isDone}"
            if(noteList[position].isDone == true){
                colorLayout.setCardBackgroundColor(R.color.do_16)
            }else{
                colorLayout.setCardBackgroundColor(resources.getColor(index,null))
            }
            colorLayout.setOnClickListener {
                ltn.onItemClick(noteList[holder.adapterPosition])
            }
            btn_delete.setOnClickListener {
                val dialog = AlertDialog.Builder(context)
                dialog.apply {
                    setTitle("Confirm Delete Task")
                    setMessage("Have you confirmed to remove the Task from the List?")
                    setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }
                    setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                        ltn.onDeleteItemClick(noteList[holder.adapterPosition])
                    }
                }.show()
            }
            Glide.with(context).load(const.listImg[position]).into(image)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Task>){
        fullList.clear()
        fullList.addAll(newList)

        noteList.clear()
        noteList.addAll(fullList)
        notifyDataSetChanged()
    }
    fun searchFilter(content: String){
        noteList.clear()
        for(nowIT in fullList){
            if(nowIT.date?.lowercase()?.contains(content.lowercase()) == true  ||
                nowIT.title?.lowercase()?.contains(content.lowercase()) == true ||
                nowIT.content?.lowercase()?.contains(content.lowercase()) == true ||
                nowIT.isDone?.toString()!!.lowercase()?.contains(content.lowercase()) == true)
                noteList.add(nowIT)
        }
        notifyDataSetChanged()
    }

    fun searchIsDone(content: String): Boolean{
        for(nowIT in fullList){
            if(nowIT.date?.lowercase()?.contains(content.lowercase()) == true )
                return true;
        }
        return false;
    }
    fun dayofTasks():ArrayList<Task>{
        return noteList
    }
    fun random(): Int{
        return R.color.do_15
    }
    interface TaskOnClickListener{
        fun onItemClick(task: Task)
        fun onDeleteItemClick(task: Task)
    }
}
package com.example.record_a_day.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.record_a_day.R
import com.example.record_a_day.data.TaskItem
import com.example.record_a_day.databinding.ItemTaskRecyclerBinding


class TaskAdapter() : RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

    private val TAG = "seok"
    var datas = mutableListOf<TaskItem>()

    interface ItemListener {
        fun onItemClick(taskItem: TaskItem?, content: String, check: Boolean)
    }

    private lateinit var mListener: ItemListener

    fun setListener(listener: ItemListener) {
        mListener = listener
    }

    inner class ViewHolder(private val binding: ItemTaskRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val taskCheck = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val taskContent = itemView.findViewById<TextView>(R.id.task_content)

        fun bind(item : TaskItem){
            taskCheck.isChecked = item.check
            taskContent.text = item.content.toString()
            taskCheck.setOnCheckedChangeListener { _item, _isChecked ->
                mListener.onItemClick(item, taskContent.text.toString(), _isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

}
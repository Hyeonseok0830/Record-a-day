package com.example.record_a_day.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.record_a_day.R
import com.example.record_a_day.data.RecordItem
import com.example.record_a_day.databinding.ItemRecordRecyclerBinding
import com.example.record_a_day.databinding.RecordFragmentBinding

class RecordAdapter() : RecyclerView.Adapter<RecordAdapter.ViewHolder>(){

    private val TAG = "seok"
    var datas = mutableListOf<RecordItem>()


    inner class ViewHolder(private val binding: ItemRecordRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val recordTitle = itemView.findViewById<TextView>(R.id.record_title)
        private val recordDate = itemView.findViewById<TextView>(R.id.record_date)
        private val recordWeather = itemView.findViewById<ImageView>(R.id.record_weather)

        fun bind(item : RecordItem){
            recordTitle.text = item.title.toString()
            recordDate.text = item.date.toString()
            var weatherImg= when(item.weather){
                "clear" -> R.drawable.clear
                "cloudy" -> R.drawable.cloudy
                "cold" -> R.drawable.cold
                "partly_sunny" -> R.drawable.partly_sunny
                "raining" -> R.drawable.raining
                "snowing" -> R.drawable.snowing
                else -> R.drawable.clear
            }
            recordWeather.load(weatherImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecordRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordAdapter.ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${datas[position].title}")
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

}
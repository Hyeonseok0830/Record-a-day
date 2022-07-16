package com.example.record_a_day.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.record_a_day.R
import com.example.record_a_day.adapter.RecordAdapter
import com.example.record_a_day.adapter.TaskAdapter
import com.example.record_a_day.data.RecordItem
import com.example.record_a_day.data.TaskItem
import com.example.record_a_day.databinding.RecordFragmentBinding
import com.example.record_a_day.databinding.TaskFragmentBinding
import com.example.record_a_day.manager.UserDataManager
import com.google.firebase.firestore.FirebaseFirestore

class TaskFragment : Fragment() {
    private var mBinding: TaskFragmentBinding? = null
    private val binding get() = mBinding!!

    var mContext: Context? = null

    private val TAG = "seok"

    lateinit var taskAdapter: TaskAdapter
    val datas = mutableListOf<TaskItem>()


    private var recyclerView: RecyclerView? = null

    val firestore = FirebaseFirestore.getInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = TaskFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFunctions()
        initRecyclerView()

    }

    private fun initFunctions() {
        binding.addTask.setOnClickListener {
            if(binding.contentTask.text.isEmpty()){
                Toast.makeText(mContext, "할 일을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var taskData = mutableMapOf(
                "id" to UserDataManager.getInstance(mContext!!).id,
                "check" to false,
                "content" to binding.contentTask.text.toString()
            )
            firestore.collection("task")
                .add(taskData)
                .addOnSuccessListener {
                    Log.d(TAG, "onCreate: Success add record info")
                    initRecyclerView()
                }
                .addOnFailureListener {
                    Log.d(TAG, "onCreate: Fail add record info")
                }
            binding.contentTask.text.clear()
        }
    }

    private fun initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ${UserDataManager.getInstance(mContext!!).id}")
        firestore.collection("task")
            .whereEqualTo("id", UserDataManager.getInstance(mContext!!).id)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "initRecyclerView: select success")
                datas.clear()
                for (document in documents) {
                    datas.apply {
                        add(
                            TaskItem(
                                document.data["check"] as Boolean,
                                document.data["content"].toString()
                            )
                        )
                    }
                }
                Log.d(TAG,"datas size ${datas.size}")
                taskAdapter = TaskAdapter()
                taskAdapter.setListener(object : TaskAdapter.ItemListener {
                    override fun onItemClick(view: View?, content: String, check: Boolean) {
                        TODO("check 상태 업데이트 로직 추가 필요")
                    }
                })
                if (!taskAdapter.datas.isEmpty())
                    taskAdapter.datas.clear()
                taskAdapter.datas = datas
                binding.recyclerView.adapter = taskAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(mContext!!)
                taskAdapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Log.e(TAG, "initRecyclerView: error! ")
            }

    }

}
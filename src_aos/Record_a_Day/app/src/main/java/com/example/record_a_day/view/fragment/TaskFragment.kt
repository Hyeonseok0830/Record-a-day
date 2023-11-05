package com.example.record_a_day.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.record_a_day.adapter.TaskAdapter
import com.example.record_a_day.data.TaskItem
import com.example.record_a_day.databinding.TaskFragmentBinding
import com.example.record_a_day.manager.UserDataManager
import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.TaskPresenter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.orhanobut.logger.Logger

class TaskFragment : Fragment(), Contractor.TaskView {
    private var mBinding: TaskFragmentBinding? = null
    private val binding get() = mBinding!!

    var mContext: Context? = null

    private val TAG = "seok"

    lateinit var taskAdapter: TaskAdapter
    val datas = mutableListOf<TaskItem>()


    private var recyclerView: RecyclerView? = null

    val firestore = FirebaseFirestore.getInstance()

    private var presenter: TaskPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = TaskPresenter(this)
    }

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
            if (binding.contentTask.text.isEmpty()) {
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
                    Logger.d("onCreate: Success add record info")
                    initRecyclerView()
                }
                .addOnFailureListener {
                    Logger.d("onCreate: Fail add record info")
                }
            binding.contentTask.text.clear()
        }
    }

    private fun initRecyclerView() {
        Logger.d("initRecyclerView: ${UserDataManager.getInstance(mContext!!).id}")
        firestore.collection("task")
            .whereEqualTo("id", UserDataManager.getInstance(mContext!!).id)
            .get()
            .addOnSuccessListener { documents ->
                Logger.d("initRecyclerView: select success")
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
                Logger.d("datas size ${datas.size}")
                taskAdapter = TaskAdapter()
                taskAdapter.setListener(object : TaskAdapter.ItemListener {
                    override fun onItemClick(taskItem: TaskItem?, content: String, check: Boolean) {
                        for (document in documents) {
                            if(content.equals(document.data["content"].toString())){
                                if(taskItem!=null) {
                                    taskItem.check = !taskItem.check
                                    val item = TaskItem(
                                        taskItem.check,
                                        taskItem.content
                                    )
                                    firestore.collection("task")
                                        .document(document.id).set(item, SetOptions.merge())
                                    taskAdapter.run { notifyDataSetChanged() }
                                }
                            }
                        }
                    }
                })
                if (!taskAdapter.datas.isEmpty())
                    taskAdapter.datas.clear()
                taskAdapter.datas = datas
                binding.recyclerView.adapter = taskAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(mContext!!)
                taskAdapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Logger.e("initRecyclerView: error! ")
            }

    }

}
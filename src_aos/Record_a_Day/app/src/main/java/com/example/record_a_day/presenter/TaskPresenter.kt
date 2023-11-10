package com.example.record_a_day.presenter

import android.content.Context
import com.example.record_a_day.adapter.TaskAdapter
import com.example.record_a_day.data.TaskItem
import com.example.record_a_day.manager.UserDataManager
import com.example.record_a_day.model.TaskModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.orhanobut.logger.Logger

class TaskPresenter(context: Context, view: Contractor.TaskView) : Contractor.TaskPresenter {
    var view: Contractor.TaskView? = null
    var context: Context? = null
    var loginModel: TaskModel? = null
    val firestore = FirebaseFirestore.getInstance()
    lateinit var taskAdapter: TaskAdapter
    val datas = mutableListOf<TaskItem>()

    interface EventListener {
        fun refreshadapter(taskAdapter: TaskAdapter)

    }

    private var eventListener: EventListener? = null

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }


    init {
        this.context = context
        this.view = view
        loginModel = TaskModel(this)
    }

    fun addTask(taskContent: String) {
        var taskData = mutableMapOf(
            "id" to UserDataManager.getInstance(context!!).id,
            "check" to false,
            "content" to taskContent
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
    }

    fun initRecyclerView() {
        Logger.d("initRecyclerView: ${UserDataManager.getInstance(context!!).id}")
        firestore.collection("task")
            .whereEqualTo("id", UserDataManager.getInstance(context!!).id)
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
                eventListener.let { eventListener?.refreshadapter(taskAdapter) }

            }.addOnFailureListener {
                Logger.e("initRecyclerView: error! ")
            }
    }
}
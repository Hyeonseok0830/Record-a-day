package com.example.record_a_day.presenter

import android.content.Context
import android.util.Log
import com.example.record_a_day.adapter.RecordAdapter
import com.example.record_a_day.data.RecordItem
import com.example.record_a_day.manager.UserDataManager
import com.example.record_a_day.model.RecordModel
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import java.util.Calendar

class RecordPresenter(context: Context, view: Contractor.RecordView) : Contractor.RecordPresenter {
    var view: Contractor.RecordView? = null
    var context: Context? = null
    var loginModel: RecordModel? = null
    val firestore = FirebaseFirestore.getInstance()
    private val TAG = "seok"
    val datas = mutableListOf<RecordItem>()
    private lateinit var recordAdapter: RecordAdapter


    interface EventListener {
        fun refreshadapter(recordAdapter: RecordAdapter)

    }

    private var eventListener: EventListener? = null

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    init {
        this.context = context
        this.view = view
        loginModel = RecordModel(this)
    }

    fun addRecord(recordData: MutableMap<String, String>) {
        firestore.collection("record")
            .add(recordData)
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

        firestore.collection("record")
            .whereEqualTo("id", UserDataManager.getInstance(context!!).id)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "initRecyclerView: select success")
                datas.clear()
                val calendar = Calendar.getInstance()
                for (document in documents) {
                    val dateSplit = document.data["date"].toString().split("/")
                    calendar.set(
                        dateSplit[0].toInt(),
                        dateSplit[1].toInt(),
                        dateSplit[2].toInt(),
                        0,
                        0,
                        0
                    )
                    datas.apply {
                        add(
                            RecordItem(
                                document.data["title"].toString(),
                                document.data["content"].toString(),
                                document.data["date"].toString(),
                                document.data["weather"].toString(),
                                calendar.timeInMillis
                            )
                        )
                    }
                    datas.sortByDescending { it.compareDate }
                }

                recordAdapter = RecordAdapter()
                recordAdapter.setListener(object : RecordAdapter.ItemListener {
                    override fun onItemDelete(recordItem: RecordItem?, title: String?) {
                        for (document in documents) {
                            if (title.equals(document.data["title"].toString())) {
                                if (recordItem != null) {
                                    datas.apply {
                                        remove(recordItem)
                                    }
                                    firestore.collection("record")
                                        .document(document.id).delete()
                                    recordAdapter.run { notifyDataSetChanged() }
                                }
                            }
                        }
                    }

                })
                if (!recordAdapter.datas.isEmpty())
                    recordAdapter.datas.clear()
                recordAdapter.datas = datas

                eventListener.let { eventListener?.refreshadapter(recordAdapter) }

            }.addOnFailureListener {
                Logger.e("initRecyclerView: error! ")
            }
    }

    fun filterItem(text: String) {
        if (text.isEmpty()) {
            initRecyclerView()
            return
        }
        Logger.d("initRecyclerView: ${UserDataManager.getInstance(context!!).id}")

        firestore.collection("record")
            .whereEqualTo("id", UserDataManager.getInstance(context!!).id)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "initRecyclerView: select success")
                datas.clear()
                val calendar = Calendar.getInstance()
                for (document in documents) {
                    if (document.data["title"].toString().contains(text)) {
                        val dateSplit = document.data["date"].toString().split("/")
                        calendar.set(
                            dateSplit[0].toInt(),
                            dateSplit[1].toInt(),
                            dateSplit[2].toInt(),
                            0,
                            0,
                            0
                        )
                        datas.apply {
                            add(
                                RecordItem(
                                    document.data["title"].toString(),
                                    document.data["content"].toString(),
                                    document.data["date"].toString(),
                                    document.data["weather"].toString(),
                                    calendar.timeInMillis
                                )
                            )
                        }
                        datas.sortByDescending { it.compareDate }
                    }
                }

                recordAdapter = RecordAdapter()
                recordAdapter.setListener(object : RecordAdapter.ItemListener {
                    override fun onItemDelete(recordItem: RecordItem?, title: String?) {
                        for (document in documents) {
                            if (title.equals(document.data["title"].toString())) {
                                if (recordItem != null) {
                                    datas.apply {
                                        remove(recordItem)
                                    }
                                    firestore.collection("record")
                                        .document(document.id).delete()
                                    recordAdapter.run { notifyDataSetChanged() }
                                }
                            }
                        }
                    }

                })
                if (!recordAdapter.datas.isEmpty())
                    recordAdapter.datas.clear()
                recordAdapter.datas = datas

                eventListener.let { eventListener?.refreshadapter(recordAdapter) }

            }.addOnFailureListener {
                Logger.e("initRecyclerView: error! ")
            }
    }


}
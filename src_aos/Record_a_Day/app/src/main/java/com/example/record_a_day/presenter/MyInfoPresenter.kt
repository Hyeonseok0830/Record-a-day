package com.example.record_a_day.presenter

import android.content.Context
import android.util.Log
import com.example.record_a_day.data.UserData
import com.example.record_a_day.manager.PreferenceManager
import com.example.record_a_day.model.MyInfoModel
import com.example.record_a_day.view.activity.LoginActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.LoadBundleTaskProgress.TaskState
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import java.util.StringTokenizer

class MyInfoPresenter(context: Context, view: Contractor.MyInfoView) : Contractor.MyInfoPresenter {
    var view: Contractor.MyInfoView? = null
    var context: Context? = null
    var myInfoModel: MyInfoModel? = null
    val firestore = FirebaseFirestore.getInstance()
    lateinit var user_info: String
    lateinit var user_data: UserData

    interface EventListener {
        fun recordCount(recordCount: Int)
        fun taskState(taskState: String)
    }

    private var eventListener: EventListener? = null

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    init {
        this.context = context
        this.view = view
        myInfoModel = MyInfoModel(this)
        user_info = view.let {
            PreferenceManager.getString(context, LoginActivity.USER_INFO_KEY)
        }.toString()
        Logger.i("onCreateView: $user_info")

        val list = arrayListOf<String>()
        StringTokenizer(user_info, "|").apply {
            while (hasMoreTokens()) {
                list.add(nextToken())
            }
        }
        user_data = UserData(list[0], list[1], "", "", "")
    }

    fun getRecordCount() {
        var count = 0
        firestore.collection("record")
            .whereEqualTo("id", user_data.id)
            .get()
            .addOnSuccessListener { documents ->
                count = documents.size()
                eventListener.let { eventListener?.recordCount(count) }
            }
            .addOnFailureListener {
                eventListener.let { eventListener?.recordCount(0) }
            }
    }

    fun getTaskState() {
        var checkCount = 0
        var state = "0/0"
        firestore.collection("task")
            .whereEqualTo("id", user_data.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var checked = document.data["check"] as Boolean
                    if (checked) checkCount++
                }
                state = checkCount.toString() + "/" + documents.size()
                eventListener.let { eventListener?.taskState(state) }
            }.addOnFailureListener{
                eventListener.let { eventListener?.taskState("") }
            }
    }

}
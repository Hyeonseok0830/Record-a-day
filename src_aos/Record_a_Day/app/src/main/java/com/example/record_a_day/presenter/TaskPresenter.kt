package com.example.record_a_day.presenter

import com.example.record_a_day.model.TaskModel
import com.google.firebase.firestore.FirebaseFirestore

class TaskPresenter(view: Contractor.TaskView) : Contractor.TaskPresenter {
    var view: Contractor.TaskView? = null
    var loginModel: TaskModel? = null
    val firestore = FirebaseFirestore.getInstance()

    init {
        this.view = view
        loginModel = TaskModel(this)
    }
}
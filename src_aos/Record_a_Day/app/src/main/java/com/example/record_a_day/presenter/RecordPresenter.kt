package com.example.record_a_day.presenter

import com.example.record_a_day.model.RecordModel
import com.google.firebase.firestore.FirebaseFirestore

class RecordPresenter(view: Contractor.RecordView) : Contractor.RecordPresenter {
    var view: Contractor.RecordView? = null
    var loginModel: RecordModel? = null
    val firestore = FirebaseFirestore.getInstance()

    init {
        this.view = view
        loginModel = RecordModel(this)
    }
}
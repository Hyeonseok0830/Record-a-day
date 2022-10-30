package com.example.record_a_day.presenter

import com.example.record_a_day.model.MyInfoModel
import com.google.firebase.firestore.FirebaseFirestore

class MyInfoPresenter(view: Contractor.MyInfoView) : Contractor.MyInfoPresenter {
    var view: Contractor.MyInfoView? = null
    var loginModel: MyInfoModel? = null
    val firestore = FirebaseFirestore.getInstance()

    init {
        this.view = view
        loginModel = MyInfoModel(this)
    }
}
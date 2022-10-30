package com.example.record_a_day.presenter

import com.example.record_a_day.model.GoalModel
import com.google.firebase.firestore.FirebaseFirestore

class GoalPresenter(view: Contractor.GoalView) : Contractor.GoalPresenter {
    var view: Contractor.GoalView? = null
    var loginModel: GoalModel? = null
    val firestore = FirebaseFirestore.getInstance()

    init {
        this.view = view
        loginModel = GoalModel(this)
    }
}
package com.example.record_a_day.model

import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.GoalPresenter

class GoalModel(goalPresenter: GoalPresenter) {
    var presenter: Contractor.GoalPresenter? = null

    init {
        presenter = goalPresenter
    }
}
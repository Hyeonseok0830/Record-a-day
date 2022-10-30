package com.example.record_a_day.model

import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.TaskPresenter

class TaskModel(taskPresenter: TaskPresenter) {
    var presenter: Contractor.TaskPresenter? = null

    init {
        presenter = taskPresenter
    }
}
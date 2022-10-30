package com.example.record_a_day.model

import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.RecordPresenter

class RecordModel(recordPresenter: RecordPresenter) {
    var presenter: Contractor.RecordPresenter? = null

    init {
        presenter = recordPresenter
    }
}
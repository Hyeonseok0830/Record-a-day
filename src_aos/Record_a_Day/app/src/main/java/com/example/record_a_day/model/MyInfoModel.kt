package com.example.record_a_day.model

import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.MyInfoPresenter

class MyInfoModel(myinfoPresenter: MyInfoPresenter) {
    var presenter: Contractor.MyInfoPresenter? = null

    init {
        presenter = myinfoPresenter
    }
}
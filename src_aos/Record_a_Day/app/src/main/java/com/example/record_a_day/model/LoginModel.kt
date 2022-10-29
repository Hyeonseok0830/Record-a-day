package com.example.record_a_day.model

import android.content.Context
import com.example.record_a_day.manager.PreferenceManager
import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.LoginPresenter
import com.example.record_a_day.view.activity.LoginActivity


class LoginModel(loginPresenter: LoginPresenter) {
    var presenter: Contractor.Presenter? = null
    init {
        this.presenter = loginPresenter
    }
    fun setPreference(context: Context, type: String, value:String){
        when(type){
            "Login"->{
                PreferenceManager.setString(context, LoginActivity.AUTO_LOGIN_KEY, value)
            }
            "User"->{
                PreferenceManager.setString(context, LoginActivity.USER_INFO_KEY, value)
            }
            else->{

            }

        }

    }

}
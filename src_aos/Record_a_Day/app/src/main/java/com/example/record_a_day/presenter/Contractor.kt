package com.example.record_a_day.presenter

import android.content.Context

public interface Contractor {
    interface View {

        fun loginResult(type:String, result: Boolean)
    }

    interface Presenter {

        fun login_btn(context: Context, inputId:String, inputPw:String, autoLogin:Boolean)

        fun autoLogin(context: Context, inputId: String, autoLogin: Boolean)

    }
}
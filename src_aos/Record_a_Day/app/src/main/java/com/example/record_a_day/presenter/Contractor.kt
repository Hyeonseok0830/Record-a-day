package com.example.record_a_day.presenter

import android.content.Context

public interface Contractor {

    //-------------------------
    //  액티비티
    //-------------------------
    /*
    * Login
    * */
    interface LoginView {
        fun loginResult(type: String, result: Boolean)
    }

    interface LoginPresenter {
        fun login_btn(context: Context, inputId: String, inputPw: String, autoLogin: Boolean)
        fun autoLogin(context: Context, inputId: String, autoLogin: Boolean)
    }


    //-------------------------
    //  프래그먼트
    //-------------------------
    /*
    * 나의 목표
    * */
    interface GoalView {

    }

    interface GoalPresenter {

    }

    /*
    * 나의 목표
    * */
    interface MyInfoView {

    }

    interface MyInfoPresenter {

    }

    /*
    * 기록
    * */
    interface RecordView {

    }

    interface RecordPresenter {

    }

    /*
    * 할 일
    * */
    interface TaskView {

    }

    interface TaskPresenter {

    }
}
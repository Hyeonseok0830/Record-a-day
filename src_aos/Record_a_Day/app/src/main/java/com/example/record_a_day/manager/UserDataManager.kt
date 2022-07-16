package com.example.record_a_day.manager

import android.content.Context
import com.example.record_a_day.LoginActivity
import com.example.record_a_day.data.UserData
import java.util.*

class UserDataManager private constructor() {

    companion object {
        @Volatile private var instance: UserData? = null

        @JvmStatic fun getInstance(context: Context): UserData =
            instance ?: synchronized(this) {
                var user_info = PreferenceManager.getString(context, LoginActivity.USER_INFO_KEY)
                val list = arrayListOf<String>()
                StringTokenizer(user_info, "|").apply {
                    while (hasMoreTokens()) {
                        list.add(nextToken())
                    }
                }
                instance ?: UserData(list[0], list[1], "", "", "").also {
                    instance = it
                }
            }
    }
}

package com.example.record_a_day

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){
    override fun onCreate() {
        super.onCreate()
        Log.d("TESTTEST","service create")
    }
    override fun onNewToken(token: String) {

        Log.d("TESTTEST","token refresh = "+token)
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("TESTTEST", remoteMessage.notification?.body.toString())
        super.onMessageReceived(remoteMessage)
    }
}
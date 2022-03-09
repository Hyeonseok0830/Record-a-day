package com.example.record_a_day

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){


    private val TAG = "seok"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"service create")
    }
    override fun onNewToken(token: String) {

        Log.d(TAG,"token refresh = "+token)
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, remoteMessage.notification?.body.toString())
        super.onMessageReceived(remoteMessage)
    }
}
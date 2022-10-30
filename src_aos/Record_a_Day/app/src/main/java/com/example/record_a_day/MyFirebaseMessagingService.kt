package com.example.record_a_day

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger

class MyFirebaseMessagingService : FirebaseMessagingService(){


    private val TAG = "seok"

    override fun onCreate() {
        super.onCreate()
        Logger.d("service create")
    }
    override fun onNewToken(token: String) {
        Logger.d("token refresh = $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Logger.d(remoteMessage.notification?.body.toString())
        super.onMessageReceived(remoteMessage)
    }
}
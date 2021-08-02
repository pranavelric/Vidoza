package com.social.vidoza.data.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("RRR", "onNewToken: ${token}")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("RRR", "onMessageReceived: ${remoteMessage}")
        if(remoteMessage!=null&&remoteMessage.data.isNotEmpty()){
            Log.d("RRR", "onMessageReceived:${remoteMessage.data} ")
        }


    }


}
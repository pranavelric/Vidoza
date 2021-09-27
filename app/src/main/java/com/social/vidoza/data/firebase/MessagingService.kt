package com.social.vidoza.data.firebase

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.social.vidoza.ui.incomingMeeting.IncomingMeetingActivity
import com.social.vidoza.utils.Constants
import com.social.vidoza.utils.transitionAnimationBundle

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("RRR", "onNewToken: ${token}")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val type = remoteMessage.data.get(Constants.REMOTE_MSG_TYPE)


        if (type != null) {
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {

                Intent(applicationContext, IncomingMeetingActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    it.putExtra(Constants.CALL_COMING, Constants.CALL_COMING)
                    it.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_TYPE)
                    )
                    it.putExtra(
                        Constants.USER_IMAGE_URL,
                        remoteMessage.data.get(Constants.USER_IMAGE_URL)
                    )
                    it.putExtra(Constants.USERNAME, remoteMessage.data.get(Constants.USERNAME))
                    it.putExtra(Constants.USER_EMAIL, remoteMessage.data.get(Constants.USER_EMAIL))
                    it.putExtra(
                        Constants.USER_PHONE_NUMBER,
                        remoteMessage.data.get(Constants.USER_PHONE_NUMBER)
                    )
                    it.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.data.get(Constants.REMOTE_MSG_INVITER_TOKEN)
                    )
                    startActivity(it, transitionAnimationBundle())
                }

            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {

                val intent = Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                intent.putExtra(
                    Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    remoteMessage.data.get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                )

                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)


            }

        }


    }


}
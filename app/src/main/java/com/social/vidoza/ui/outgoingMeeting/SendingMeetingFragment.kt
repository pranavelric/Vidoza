package com.social.vidoza.ui.outgoingMeeting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.messaging.FirebaseMessaging
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.data.remote.ApiService
import com.social.vidoza.databinding.SendingMeetingFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SendingMeetingFragment : Fragment() {


    private val viewModel: SendingMeetingViewModel by lazy {
        ViewModelProvider(this).get(SendingMeetingViewModel::class.java)
    }
    private lateinit var sendingMeetingFragmentBinding: SendingMeetingFragmentBinding
    private var user: User? = null
    private var currentUser: User? = null

    private var call_type: String? = null
    private var inviterToken: String? = null
    private var meetingRoom: String? = null
    private var meetingType: String? = null

    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?
            currentUser = it.getSerializable(Constants.CURRENT_USER) as User?
            call_type = it.getString(Constants.CALL_TYPE)
        }

        meetingType = call_type



        CoroutineScope(Dispatchers.Main).launch {
            FirebaseMessaging.getInstance().apply {
                token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        inviterToken = it.result

                        if (call_type != null && currentUser != null && user != null) {
                            user!!.fcm_token?.let { initiateMeeting(call_type!!, it) }
                        }

                    } else {
                        it.exception?.message?.let { it1 -> context?.toast(it1) }
                    }
                }.await()

            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sendingMeetingFragmentBinding =
            SendingMeetingFragmentBinding.inflate(inflater, container, false);

        return sendingMeetingFragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendingMeetingFragmentBinding.expandedImage.getBackgroundImage(Uri.parse((activity as MainActivity).sharedPrefrences.getBrackgroundImage()))



        setData()
        setCliclListeners()
    }

    private fun setCliclListeners() {

        sendingMeetingFragmentBinding.cancelBtnCard.setOnClickListener {

            //  findNavController().navigateUp()

            if (user != null) {
                user!!.fcm_token?.let { it1 -> cancelInvitation(it1) }
            }


        }
        sendingMeetingFragmentBinding.cancleBtn.setOnClickListener {
            // findNavController().navigateUp()
            if (user != null) {
                user!!.fcm_token?.let { it1 -> cancelInvitation(it1) }
            }
        }


    }

    private fun setData() {

        if (call_type.equals("video")) {

            sendingMeetingFragmentBinding.callType.setImageResource(R.drawable.ic_baseline_videocam_24)

        } else {
            sendingMeetingFragmentBinding.callType.setImageResource(R.drawable.ic_baseline_call_24)
        }



        sendingMeetingFragmentBinding.username.text = user?.name ?: user?.phoneNumber



        sendingMeetingFragmentBinding.progressBar.visible()
        Glide.with(this).load(user?.imageUrl)
            .placeholder(R.drawable.ic_baseline_person_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    sendingMeetingFragmentBinding.progressBar.gone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {

                    sendingMeetingFragmentBinding.progressBar.gone()
                    return false
                }

            })
            .into(sendingMeetingFragmentBinding.profilePic)


    }


    private fun initiateMeeting(meetingType: String, receiverToken: String) {

        try {
            var token = JSONArray()
            token.put(receiverToken)

            var body = JSONObject()
            body.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION)
            body.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType)
            body.put(Constants.USERNAME, currentUser?.name)
            body.put(Constants.USER_EMAIL, currentUser?.email)
            body.put(Constants.USER_IMAGE_URL, currentUser?.imageUrl)
            body.put(Constants.USER_PHONE_NUMBER, currentUser?.phoneNumber)
            body.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken)

            meetingRoom = currentUser?.uid + UUID.randomUUID().toString().substring(0, 5)

            body.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom)

            var dataBody = JSONObject()
            dataBody.put(Constants.REMOTE_MSG_DATA, body)
            dataBody.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token)

            sendRemoteMessage(dataBody.toString(), Constants.REMOTE_MSG_INVITATION)

        } catch (e: Exception) {
            e.message?.let { context?.toast(it) }
            findNavController().navigateUp()
        }


    }


    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {

        apiService.sendRemoteMessages(Constants.getHeaders(), remoteMessageBody).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {

                    if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        context?.toast("Invitation send successfully")
                    } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {

                        context?.toast("Invitation Canceled")
                        findNavController().navigateUp()
                    }


                } else {
                    context?.toast(response.message())
                    findNavController().navigateUp()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { context?.toast(it) }
                findNavController().navigateUp()
            }

        })

    }


    private fun cancelInvitation(receiverToken: String) {

        try {

            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(
                Constants.REMOTE_MSG_INVITATION_RESPONSE,
                Constants.REMOTE_MSG_INVITATION_CANCELED
            )

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE)


        } catch (e: Exception) {

            e.message?.let { context?.toast(it) }
            findNavController().navigateUp()


        }


    }


    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {


            val type = intent?.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (type != null) {

                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {


                    try {
                        var serverUrl = URL("https://meet.jit.si")


                        val optionsBuilder: JitsiMeetConferenceOptions.Builder =
                            JitsiMeetConferenceOptions.Builder()
                                .setServerURL(serverUrl)
                                .setRoom(meetingRoom)

                                .setWelcomePageEnabled(false)
                                .setConfigOverride("requireDisplayName", false)

                        if(meetingType.equals("audio")){
                            optionsBuilder.setAudioOnly(true)
                        }

                        JitsiMeetActivity.launch(context, optionsBuilder.build())

                        findNavController().navigateUp()


                    } catch (e: Exception) {

                        e.message?.let { context?.toast(it) }

                        findNavController().navigateUp()
                    }


                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    context?.toast("Invitation rejected")
                    findNavController().navigateUp()
                }

            }

        }

    }


    override fun onStart() {
        super.onStart()
        context?.applicationContext?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                invitationResponseReceiver,
                IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            )
        }
        (activity as MainActivity).setFullScreen()

    }

    override fun onStop() {
        super.onStop()


        context?.applicationContext?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(invitationResponseReceiver)
        }

    }

}
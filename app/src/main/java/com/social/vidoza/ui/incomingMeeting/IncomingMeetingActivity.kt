package com.social.vidoza.ui.incomingMeeting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.social.vidoza.R
import com.social.vidoza.data.remote.ApiService
import com.social.vidoza.databinding.ActivityIncomingMeetingBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.*
import com.thecode.aestheticdialogs.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class IncomingMeetingActivity : AppCompatActivity() {


    val mainViewModel: InComingMeetingViewModel by lazy {
        ViewModelProvider(this)[InComingMeetingViewModel::class.java]
    }

    @Inject
    lateinit var networkConnection: NetworkConnection

    @Inject
    lateinit var networkHelper: NetworkHelper
    lateinit var dialog: AestheticDialog.Builder

    @Inject
    lateinit var sharedPrefrences: MySharedPrefrences

    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var apiService: ApiService


    private lateinit var binding: ActivityIncomingMeetingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        networkConnection.observe(this) {
            if (it) {
                hideNetworkDialog()


            } else {
                showNetworkDialog()
            }


        }

        binding.expandedImage.getBackgroundImage(Uri.parse(sharedPrefrences.getBrackgroundImage()))

        setData()
        setMyClickListeners()

    }

    private fun setMyClickListeners() {
        binding.cancelBtnCard.setOnClickListener {


            intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)?.let { it1 ->
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_REJECTED,
                    it1
                )
            }

            Intent(applicationContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it, transitionAnimationBundle())
            }

            finish()
        }
        binding.cancelBtn.setOnClickListener {

            intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)?.let { it1 ->
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_REJECTED,
                    it1
                )
            }

            Intent(applicationContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it, transitionAnimationBundle())
            }
            finish()
        }


        binding.acceptBtnCard.setOnClickListener {
            intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)?.let { it1 ->
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    it1
                )
            }

        }

        binding.acceptBtn.setOnClickListener {
            intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)?.let { it1 ->
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    it1
                )
            }
        }


    }

    private fun setData() {

        var meetingType = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE)

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                binding.callType.setImageResource(R.drawable.ic_baseline_videocam_24)
            } else {
                binding.callType.setImageResource(R.drawable.ic_baseline_call_24)
            }
        }

        Glide.with(this).load(intent.getStringExtra(Constants.USER_IMAGE_URL))
            .placeholder(R.drawable.ic_baseline_person_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.profilePic)

        var name = intent.getStringExtra(Constants.USERNAME)
        binding.username.text = name ?: intent.getStringExtra(Constants.USER_PHONE_NUMBER)


    }


    private fun hideNetworkDialog() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    private fun showNetworkDialog() {


        dialog = AestheticDialog.Builder(this, DialogStyle.EMOTION, DialogType.ERROR)
            .setTitle("No internet")
            .setMessage("Please check your internet connection")
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            })
        dialog.show()


    }


    private fun sendInvitationResponse(type: String, receiverToken: String) {

        try {

            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), type)


        } catch (e: Exception) {

            e.message?.let { toast(it) }
            Intent(applicationContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it, transitionAnimationBundle())
            }

            finish()


        }


    }


    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {

        apiService.sendRemoteMessages(Constants.getHeaders(), remoteMessageBody).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {

                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        toast("Invitation Accepted")
                    } else {
                        toast("Invitation Rejected")
                    }

                } else {
                    toast(response.message())

                    Intent(applicationContext, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it, transitionAnimationBundle())
                    }

                    finish()

                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { toast(it) }
                Intent(applicationContext, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it, transitionAnimationBundle())
                }

                finish()
            }

        })

    }


    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Log.d("RRR", "onReceive: broadcastr ")

            val type = intent?.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (type != null) {

                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELED)) {

                    context?.toast("Invitation cancelled")
                    Intent(applicationContext, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it, transitionAnimationBundle())
                    }

                    finish()

                }

            }

        }

    }


    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        )

        setFullScreenForNotch()
        setFullScreen()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(invitationResponseReceiver)
    }


}
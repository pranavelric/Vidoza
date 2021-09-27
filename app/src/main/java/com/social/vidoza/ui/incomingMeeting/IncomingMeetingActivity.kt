package com.social.vidoza.ui.incomingMeeting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.social.vidoza.R
import com.social.vidoza.databinding.ActivityIncomingMeetingBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.*
import com.thecode.aestheticdialogs.*
import dagger.hilt.android.AndroidEntryPoint
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

            Intent(applicationContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it, transitionAnimationBundle())
            }

            finish()
        }
        binding.cancelBtn.setOnClickListener {
            Intent(applicationContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it, transitionAnimationBundle())
            }
            finish()
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


    override fun onStart() {
        super.onStart()
        setFullScreenForNotch()
        setFullScreen()
    }


}
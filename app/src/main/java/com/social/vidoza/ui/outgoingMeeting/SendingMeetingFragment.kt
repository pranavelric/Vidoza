package com.social.vidoza.ui.outgoingMeeting

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.SendingMeetingFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.*

class SendingMeetingFragment : Fragment() {


    private val viewModel: SendingMeetingViewModel by lazy {
        ViewModelProvider(this).get(SendingMeetingViewModel::class.java)
    }
    private lateinit var sendingMeetingFragmentBinding: SendingMeetingFragmentBinding

    private var user: User? = null
    private var call_type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?
            call_type = it.getString(Constants.CALL_TYPE)
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

            findNavController().navigateUp()

        }
        sendingMeetingFragmentBinding.cancleBtn.setOnClickListener {
            findNavController().navigateUp()
        }



    }

    private fun setData() {

        if(call_type.equals("video")){

            sendingMeetingFragmentBinding.callType.setImageResource(R.drawable.ic_baseline_videocam_24)

        }else{
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

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreen()

    }

}
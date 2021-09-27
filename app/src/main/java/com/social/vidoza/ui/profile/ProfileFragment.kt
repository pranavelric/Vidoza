package com.social.vidoza.ui.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.marcoscg.dialogsheet.DialogSheet
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.ProfileFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.*
import com.social.vidoza.utils.Constants.USERNAME
import com.social.vidoza.utils.Constants.USERS_BUNDLE_OBJ
import com.social.vidoza.utils.Constants.USER_EMAIL
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog


@AndroidEntryPoint
class ProfileFragment : Fragment() {


    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
    private lateinit var profileFragmentBinding: ProfileFragmentBinding
    private var user: User? = null
    private lateinit var dialog: AlertDialog



    companion object {
        const val IMAGE_REQ_CODE = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(USERS_BUNDLE_OBJ) as User?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileFragmentBinding = ProfileFragmentBinding.inflate(inflater, container, false)
//        profileFragmentBinding.toolbar.setOnMenuItemClickListener {
//            onOptionsItemSelected(it)
//        }
//
    //    setHasOptionsMenu(true)
        return profileFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       profileFragmentBinding.background.getBackgroundImage(Uri.parse((activity as MainActivity).sharedPrefrences.getBrackgroundImage()))


        setData()
        setSlidingBehaviour()
        setClickListeners()
    }





    private fun setData() {
        setImageFromUrl(user?.imageUrl)
        profileFragmentBinding.name.text = user?.name
        profileFragmentBinding.username.text = user?.name
        profileFragmentBinding.phone.text = user?.phoneNumber
        profileFragmentBinding.email.text = user?.email
    }

    private fun setImageFromUrl(imageUrl: String?) {

        profileFragmentBinding.progressBar.visible()


        Glide.with(this).load(imageUrl)
            .placeholder(R.drawable.ic_baseline_person_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {

                    profileFragmentBinding.progressBar.gone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {

                    profileFragmentBinding.progressBar.gone()
                    return false
                }

            })
            .into(profileFragmentBinding.profilePic)

    }

    private fun setClickListeners() {


        profileFragmentBinding.editEmailBtn.setOnClickListener {
            profileFragmentBinding.editEmail.visible()
            profileFragmentBinding.email.gone()
            profileFragmentBinding.editEmailBtn.gone()
            profileFragmentBinding.editEmailCloseBtn.visible()
            profileFragmentBinding.editEmailDoneBtn.visible()
        }
        profileFragmentBinding.editNameBtn.setOnClickListener {
            profileFragmentBinding.editName.visible()
            profileFragmentBinding.name.gone()
            profileFragmentBinding.editNameBtn.gone()
            profileFragmentBinding.editNameCloseBtn.visible()
            profileFragmentBinding.editNameDoneBtn.visible()
        }

        profileFragmentBinding.editEmailCloseBtn.setOnClickListener {
            profileFragmentBinding.editEmailBtn.visible()
            profileFragmentBinding.editEmailCloseBtn.gone()
            profileFragmentBinding.editEmailDoneBtn.gone()
            profileFragmentBinding.editEmail.gone()
            profileFragmentBinding.email.visible()
        }
        profileFragmentBinding.editPhoneCloseBtn.setOnClickListener {
            profileFragmentBinding.editPhoneBtn.visible()
            profileFragmentBinding.editPhoneCloseBtn.gone()
            profileFragmentBinding.editPhoneDoneBtn.gone()
            profileFragmentBinding.editPhone.gone()
            profileFragmentBinding.phone.visible()
        }
        profileFragmentBinding.editNameCloseBtn.setOnClickListener {
            profileFragmentBinding.editNameBtn.visible()
            profileFragmentBinding.editNameCloseBtn.gone()
            profileFragmentBinding.editNameDoneBtn.gone()
            profileFragmentBinding.editName.gone()
            profileFragmentBinding.name.visible()
        }
        profileFragmentBinding.editEmailDoneBtn.setOnClickListener {
            profileFragmentBinding.editEmailBtn.visible()
            profileFragmentBinding.editEmailCloseBtn.gone()
            profileFragmentBinding.editEmailDoneBtn.gone()
            profileFragmentBinding.editEmail.gone()
            profileFragmentBinding.email.visible()
            if (validateEditText(profileFragmentBinding.editEmail)) {
                upDateUserInfo(USER_EMAIL, profileFragmentBinding.editEmail.text.toString())
            }

        }
        profileFragmentBinding.editNameDoneBtn.setOnClickListener {
            profileFragmentBinding.editNameBtn.visible()
            profileFragmentBinding.editNameCloseBtn.gone()
            profileFragmentBinding.editNameDoneBtn.gone()
            profileFragmentBinding.editName.gone()
            profileFragmentBinding.name.visible()
            if (validateEditText(profileFragmentBinding.editName)) {
                upDateUserInfo(USERNAME, profileFragmentBinding.editName.text.toString())
            }
        }

        profileFragmentBinding.profilePic.setOnClickListener {
            selectImageFromGallery()
        }


    }

    private fun upDateUserInfo(updateType: String, updatedValue: String) {
        showDialog("Updating info")
        viewModel.updateUserInfo(updateType, updatedValue)
        viewModel.updateLiveData.observe(viewLifecycleOwner, { updateStatus ->
            when (updateStatus) {
                is ResponseState.Success -> {
                    updateStatus.data?.let { profileFragmentBinding.root.snackbar(it) }
                    getData()
                    dismissDialog()
                }
                is ResponseState.Error -> {
                    updateStatus.message?.let { profileFragmentBinding.root.snackbar(it) }
                    dismissDialog()
                }
                is ResponseState.Loading -> {
                }
            }
        })

    }

    private fun getData() {
        profileFragmentBinding.progressBar.visible()

        user?.uid?.let { viewModel.getUserFromDataBase(it) }
        viewModel.userLiveData.observe(viewLifecycleOwner, { userStatus ->
            when (userStatus) {
                is ResponseState.Success -> {
                    user = userStatus.data
                    updateUi()
                    profileFragmentBinding.progressBar.gone()
                }
                is ResponseState.Loading -> {

                }
                is ResponseState.Error -> {

                    userStatus.message?.let {profileFragmentBinding.root.snackbar(it) }
                    profileFragmentBinding.progressBar.gone()
                }
            }


        })

    }

    private fun updateUi() {
        profileFragmentBinding.name.text = user?.name
        profileFragmentBinding.username.text = user?.name
        profileFragmentBinding.phone.text = user?.phoneNumber
        profileFragmentBinding.email.text = user?.email
    }

    private fun validateEditText(editText: EditText): Boolean {

        return if (editText.text.isNullOrBlank()) {
            editText.error = "please fill the filed"
            false
        } else {
            true
        }
    }

    private fun selectImageFromGallery() {


        val dialogSheet = context?.let {
            DialogSheet(it)
                .setTitle(R.string.app_name)
                .setMessage("Do you want to change your profile picture")
                .setColoredNavigationBar(true)
                .setTitleTextSize(20) // In SP
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok) {
                    chooseImage()
                }
                .setNegativeButton(android.R.string.cancel) {
                }
                .setRoundedCorners(true) // Default value is true
                .setBackgroundColor(Color.WHITE) // Your custom background color
                .setButtonsColorRes(R.color.primary_color)
                .setNegativeButtonColorRes(R.color.red)
                .show()
        }
    }

    private fun chooseImage() {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .imageEngine(GlideEngine())
            .thumbnailScale(0.85f)
            .showPreview(false) // Default is `true`
            .forResult(IMAGE_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQ_CODE && resultCode == RESULT_OK) {
            val mSelected = Matisse.obtainResult(data)

            if (mSelected.isNotEmpty()) {
                profileFragmentBinding.profilePic.setImageURI(mSelected[0])
                uploadImageToFirestore(mSelected[0])
            }


        }
    }

    private fun uploadImageToFirestore(uri: Uri) {


        showDialog("Uploading image")



        user?.uid?.let { viewModel.uploadImageInFirebaseStorage(it, uri) }
        viewModel.uploadLiveData.observe(viewLifecycleOwner, { dataState ->


            when (dataState) {
                is ResponseState.Success -> {
                    dataState.data?.let { profileFragmentBinding.root.snackbar(it) }
                    dismissDialog()
                }
                is ResponseState.Error -> {
                    dataState.message?.let { profileFragmentBinding.root.snackbar(it) }
                    dismissDialog()
                }
                is ResponseState.Loading -> {
                }
            }
        })
    }

    private fun dismissDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun showDialog(mesg: String) {
        dialog = SpotsDialog.Builder()
            .setContext(context)
            .setTheme(R.style.CustomProgressDialog)
            .setMessage(mesg)
            .setCancelable(false)
            .build()
        dialog.show()

    }


    private fun setSlidingBehaviour() {
        val behavior = BottomSheetBehavior.from(profileFragmentBinding.bottomSheet)
        if (checkAboveKitkat()) {

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                        profileFragmentBinding.bottomSheet.setPadding(0, 0, 0, 0)

                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        activity?.getStatusBarHeight()?.let { bottomSheet.setPadding(0, it, 0, 0) }

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    if (slideOffset < 0.5F) {
                        profileFragmentBinding.tempView.alpha = 0.5F

                    } else {
                        profileFragmentBinding.tempView.alpha = (slideOffset)

                    }
                    bottomSheet.setPadding(
                        0,
                        (slideOffset * activity?.getStatusBarHeight()!!).toInt(),
                        0,
                        0
                    )
                }
            })
        }

    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//
//            R.id.action_logout -> {
//                (activity as MainActivity).firebaseAuth.signOut()
//                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
//                true
//            }
//            else -> {
//                false
//            }
//        }
//    }
//

























}
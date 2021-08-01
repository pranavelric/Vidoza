package com.social.vidoza.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.ProfileFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.Constants.USERS_BUNDLE_OBJ
import com.social.vidoza.utils.checkAboveKitkat
import com.social.vidoza.utils.getStatusBarHeight

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
        profileFragmentBinding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        setHasOptionsMenu(true)
        return profileFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setSlidingBehaviour()
        setUIAccordingToLoginMethod()
        setClickListeners()
    }





    private fun setData() {
        setImageFromUrl(user?.imageUrl)
        binding.name.text = user?.name
        binding.username.text = user?.name
        binding.phone.text = user?.phoneNumber
        binding.email.text = user?.email
    }

    private fun setImageFromUrl(imageUrl: String?) {

        binding.progressBar.visible()


        Glide.with(this).load(imageUrl)
            .placeholder(R.drawable.ic_baseline_person_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {

                    binding.progressBar.gone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {

                    binding.progressBar.gone()
                    return false
                }

            })
            .into(binding.profilePic)

    }

    private fun setClickListeners() {


        binding.editEmailBtn.setOnClickListener {
            binding.editEmail.visible()
            binding.email.gone()
            binding.editEmailBtn.gone()
            binding.editEmailCloseBtn.visible()
            binding.editEmailDoneBtn.visible()
        }
        binding.editNameBtn.setOnClickListener {
            binding.editName.visible()
            binding.name.gone()
            binding.editNameBtn.gone()
            binding.editNameCloseBtn.visible()
            binding.editNameDoneBtn.visible()
        }
        binding.editPassBtn.setOnClickListener {
            binding.passName.visible()
            binding.pass.gone()
            binding.editPassBtn.gone()
            binding.editPassCloseBtn.visible()
            binding.editPassDoneBtn.visible()
        }
        binding.editPhoneBtn.setOnClickListener {
            binding.editPhone.visible()
            binding.phone.gone()
            binding.editPhoneBtn.gone()
            binding.editPhoneCloseBtn.visible()
            binding.editPhoneDoneBtn.visible()
        }

        binding.editEmailCloseBtn.setOnClickListener {
            binding.editEmailBtn.visible()
            binding.editEmailCloseBtn.gone()
            binding.editEmailDoneBtn.gone()
            binding.editEmail.gone()
            binding.email.visible()
        }
        binding.editPhoneCloseBtn.setOnClickListener {
            binding.editPhoneBtn.visible()
            binding.editPhoneCloseBtn.gone()
            binding.editPhoneDoneBtn.gone()
            binding.editPhone.gone()
            binding.phone.visible()
        }
        binding.editNameCloseBtn.setOnClickListener {
            binding.editNameBtn.visible()
            binding.editNameCloseBtn.gone()
            binding.editNameDoneBtn.gone()
            binding.editName.gone()
            binding.name.visible()
        }
        binding.editPassCloseBtn.setOnClickListener {
            binding.editPassBtn.visible()
            binding.editPassCloseBtn.gone()
            binding.editPassDoneBtn.gone()
            binding.passName.gone()
            binding.pass.visible()
        }
        binding.editEmailDoneBtn.setOnClickListener {
            binding.editEmailBtn.visible()
            binding.editEmailCloseBtn.gone()
            binding.editEmailDoneBtn.gone()
            binding.editEmail.gone()
            binding.email.visible()
            if (validateEditText(binding.editEmail)) {
                upDateUserInfo(USER_EMAIL, binding.editEmail.text.toString())
            }

        }
        binding.editPhoneDoneBtn.setOnClickListener {
            binding.editPhoneBtn.visible()
            binding.editPhoneCloseBtn.gone()
            binding.editPhoneDoneBtn.gone()
            binding.editPhone.gone()
            binding.phone.visible()
            if (validateEditText(binding.editPhone)) {
                upDateUserInfo(USER_PHONE_NUMBER, binding.editPhone.text.toString())
            }

        }
        binding.editNameDoneBtn.setOnClickListener {
            binding.editNameBtn.visible()
            binding.editNameCloseBtn.gone()
            binding.editNameDoneBtn.gone()
            binding.editName.gone()
            binding.name.visible()
            if (validateEditText(binding.editName)) {
                upDateUserInfo(USERNAME, binding.editName.text.toString())
            }
        }
        binding.editPassDoneBtn.setOnClickListener {
            binding.editPassBtn.visible()
            binding.editPassCloseBtn.gone()
            binding.editPassDoneBtn.gone()
            binding.passName.gone()
            binding.pass.visible()
            if (validateEditText(binding.passName)) {
                upDateUserInfo(USER_PASS, binding.passName.text.toString())
            }
        }

        binding.profilePic.setOnClickListener {
            selectImageFromGallery()
        }


    }

    private fun upDateUserInfo(updateType: String, updatedValue: String) {
        showDialog("Updating info")
        viewModel.updateUserInfo(updateType, updatedValue)
        viewModel.updateLiveData.observe(viewLifecycleOwner, { updateStatus ->
            when (updateStatus) {
                is ResponseState.Success -> {
                    updateStatus.data?.let { binding.root.snackbar(it) }
                    getData()
                    dismissDialog()
                }
                is ResponseState.Error -> {
                    updateStatus.message?.let { binding.root.snackbar(it) }
                    dismissDialog()
                }
                is ResponseState.Loading -> {
                }
            }
        })

    }

    private fun getData() {
        binding.progressBar.visible()

        user?.uid?.let { viewModel.getUserFromDataBase(it) }
        viewModel.userLiveData.observe(viewLifecycleOwner, { userStatus ->
            when (userStatus) {
                is ResponseState.Success -> {
                    user = userStatus.data
                    updateUi()
                    binding.progressBar.gone()
                }
                is ResponseState.Loading -> {

                }
                is ResponseState.Error -> {

                    userStatus.message?.let { binding.root.snackbar(it) }
                    binding.progressBar.gone()
                }
            }


        })

    }

    private fun updateUi() {
        binding.name.text = user?.name
        binding.username.text = user?.name
        binding.phone.text = user?.phoneNumber
        binding.email.text = user?.email
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_logout -> {
                (activity as MainActivity).firebaseAuth.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                true
            }
            else -> {
                false
            }
        }
    }


























}
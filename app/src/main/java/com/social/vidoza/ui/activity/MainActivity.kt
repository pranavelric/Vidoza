package com.social.vidoza.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.social.vidoza.BuildConfig
import com.social.vidoza.R
import com.social.vidoza.databinding.ActivityMainBinding
import com.social.vidoza.utils.*
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding: ActivityMainBinding;
    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.main_nav_host_fragment)
    }
    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    @Inject
    lateinit var sharedPrefrences: MySharedPrefrences

    @Inject
    lateinit var networkConnection: NetworkConnection

    @Inject
    lateinit var networkHelper: NetworkHelper
    lateinit var dialog: AestheticDialog.Builder

    lateinit var firebaseAuth: FirebaseAuth
//    private val permList: Array<String> = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
        firebaseAuth = FirebaseAuth.getInstance()


        networkConnection.observe(this) {
            if (it) {
                hideNetworkDialog()


            } else {
                showNetworkDialog()
            }


        }

        if (networkHelper.isNetworkConnected() && this::firebaseAuth.isInitialized && firebaseAuth.currentUser != null)
            checkForUpdates()

        observeData()


    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }


    private fun checkForUpdates() {


        mainViewModel.checForUpdates(BuildConfig.VERSION_CODE)
        mainViewModel.checkForUpdate.observe(this, {
            when (it) {
                is ResponseState.Success -> {
                    it.data?.let { isUpdate ->
                        if (isUpdate) {
                            this@MainActivity.showCustomDialog(
                                "Update",
                                "New Update Available",
                                false,
                                DialogStyle.FLAT,
                                DialogType.WARNING,
                                DialogAnimation.SHRINK
                            )
                        }
                    }
                }
                is ResponseState.Error -> {
                    this.toast("Some error occured while checking for update!!")
                }
                is ResponseState.Loading -> {
                }

            }
        })


    }

    private fun observeData() {

    }


    override fun onStart() {
        super.onStart()
        setFullScreenForNotch()
        setFullScreenWithBtmNav()
    }

    private fun hideNetworkDialog() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    private fun showNetworkDialog() {

        this@MainActivity.showCustomDialog(
            "No internet",
            "Please check your internet connection",
            false,
            DialogStyle.EMOTION,
            DialogType.ERROR,
            DialogAnimation.SHRINK
        )


    }


}
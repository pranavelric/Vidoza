package com.social.vidoza.ui.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.social.vidoza.BuildConfig
import com.social.vidoza.R
import com.social.vidoza.databinding.ActivityMainBinding
import com.social.vidoza.utils.*
import com.thecode.aestheticdialogs.*
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


    private val permList: Array<String> = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    companion object {
        private const val PERMISSION_CODE = 121
    }


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


        if (!checkForPermission(READ_EXTERNAL_STORAGE)) {
            askPermission(permList)
        }
        if (!checkForPermission(WRITE_EXTERNAL_STORAGE)) {
            askPermission(permList)
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




    private fun hideNetworkDialog() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    private fun showNetworkDialog() {


     dialog =    AestheticDialog.Builder(this, DialogStyle.EMOTION,   DialogType.ERROR)
            .setTitle("No internet")
            .setMessage("Please check your internet connection")
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation( DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            })
          dialog.show()



    }


    private fun checkForPermission(permission: String): Boolean {

        return ActivityCompat.checkSelfPermission(
            this@MainActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            val bool = grantResults[0] == PackageManager.PERMISSION_GRANTED
            onPermissionResult(bool)
            val bool1 = grantResults[1] == PackageManager.PERMISSION_GRANTED
            onPermissionResult(bool1)
        }

    }

    private fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            askPermission(permList)
        }
    }

    private fun askPermission(permList: Array<String>) {
        ActivityCompat.requestPermissions(this@MainActivity, permList, PERMISSION_CODE)
    }


    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(invitationResponseReceiver)

    }




    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Log.d("RRR", "onReceive: broadcastmain")
            val type = intent?.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (type != null) {

                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                    context?.toast("Invitation accepted")

                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    context?.toast("Invitation rejected")

                    navController.navigateUp()


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
        setFullScreenWithBtmNav()
    }









}
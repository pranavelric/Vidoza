package com.social.vidoza.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.firebase.messaging.FirebaseMessaging
import com.social.vidoza.R
import com.social.vidoza.adapter.UserListAdapter
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.HomeFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.*
import com.social.vidoza.utils.Constants.CALL_TYPE
import com.social.vidoza.utils.Constants.CURRENT_USER
import com.social.vidoza.utils.Constants.USERS_BUNDLE_OBJ
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.ibrahimsn.lib.OnItemSelectedListener
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    private lateinit var homeFragmentBinding: HomeFragmentBinding

    private var user: User? = null

    @Inject
    lateinit var userListAdapter: UserListAdapter

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
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)
        homeFragmentBinding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        setHasOptionsMenu(true)
        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeFragmentBinding.expandedImage.getBackgroundImage(Uri.parse((activity as MainActivity).sharedPrefrences.getBrackgroundImage()))



        CoroutineScope(Dispatchers.Main).launch {
            FirebaseMessaging.getInstance().apply {
                token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        user?.uid?.let { it1 -> viewModel.updateFCMToken(it.result, it1) }
                    }
                }.await()

            }
        }


        getData()
        setData()
        observeData()
        setClickListeners()
    }


    private fun observeData() {

        viewModel.userListLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    homeFragmentBinding.refresh.isRefreshing = false

                    homeFragmentBinding.progressBar.gone()

                    it.data?.let { userList ->

                        if (!userList.isNullOrEmpty()) {
                            homeFragmentBinding.emptyLay.gone()
                            userListAdapter.submitList(userList)
                        } else {


                            homeFragmentBinding.emptyLay.visible()
                        }

                    }
                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {
                    homeFragmentBinding.refresh.isRefreshing = false
                    homeFragmentBinding.progressBar.gone()
                    it?.message?.let { it1 -> context?.toast(it1) }
                    homeFragmentBinding.emptyLay.visible()
                }

            }
        })


    }


    private fun getData() {

        homeFragmentBinding.progressBar.visible()
        user?.uid?.let { viewModel.getUserList(it) }
//        user?.uid?.let { viewModel.getUserFromDataBase(it) }
//
//        viewModel.userLiveData.observe(viewLifecycleOwner, { userTask ->
//            when (userTask) {
//                is ResponseState.Success -> {
//                    user = userTask.data
//
//
//                    homeFragmentBinding.topLay.userCoinsAmount.text = user?.coins?.let { roundToTwoDecimal(it).toString() }
//                }
//                is ResponseState.Error -> {
//                    userTask.message?.let { binding.root.snackbar(it) }
//                }
//                is ResponseState.Loading -> {
//
//                }
//            }
//        })

    }


    private fun setData() {
        homeFragmentBinding.userRc.apply {
            adapter = userListAdapter
        }
    }


    private fun setClickListeners() {

        userListAdapter.setOnCallItemClickListener { user, i ->

            startCallMeeting(user, i)

        }
        userListAdapter.setOnVideoItemClickListener { user, i ->
            startVideoMeeting(user, i)

        }

        homeFragmentBinding.refresh.setOnRefreshListener {
            user?.uid?.let { viewModel.getUserList(it) }
            homeFragmentBinding.refresh.isRefreshing = true
        }


        homeFragmentBinding.bottomBar.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelect(pos: Int): Boolean {
                when (pos) {
                    0 -> {
                        return true
                    }

                    1 -> {
                        val bundle = Bundle().apply {
                            putSerializable(USERS_BUNDLE_OBJ, user)
                        }

                        findNavController().navigate(
                            R.id.action_homeFragment_to_profileFragment,
                            bundle
                        )

                        return true
                    }

                    else -> {

                        return false
                    }
                }

            }
        }


    }

    private fun startVideoMeeting(userr: User?, i: Int) {
        if (userr == null || userr?.fcm_token.isNullOrBlank()) {
            homeFragmentBinding.root.snackbar("User is not available for meeting")
        } else {

            val bundle = Bundle().apply {
                putSerializable(USERS_BUNDLE_OBJ, userr)
                putSerializable(CURRENT_USER, user)
                putString(CALL_TYPE, "video")


            }

            findNavController().navigate(
                R.id.action_homeFragment_to_sendingMeetingFragment,
                bundle
            )


        }


    }

    private fun startCallMeeting(user: User?, pos: Int) {
        if (user == null || user?.fcm_token.isNullOrBlank()) {
            homeFragmentBinding.root.snackbar("User is not available for meeting")
        } else {

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.action_profile -> {
                val bundle = Bundle().apply {
                    putSerializable(USERS_BUNDLE_OBJ, user)
                }
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
                return true
            }
            R.id.action_logout -> {

                user?.uid?.let { viewModel.deleteTokenFromUser(it) }
                signOut()


                return true
            }
            R.id.action_share -> {
                activity?.share("Playstore link", "text")

                return true
            }

            R.id.action_settings -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_settingsFragment,

                    )
                return true
            }


            else -> {
                return false
            }
        }
    }

    private fun signOut() {


        CoroutineScope(Dispatchers.Main).launch {
            FirebaseMessaging.getInstance().apply {
                deleteToken().addOnCompleteListener {
                }.await()
            }
        }

        (activity as MainActivity).firebaseAuth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreenWithBtmNav()

    }


}
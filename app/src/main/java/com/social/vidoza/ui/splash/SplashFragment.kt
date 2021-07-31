package com.social.vidoza.ui.splash

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.SplashFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.Constants.USERS_BUNDLE_OBJ
import androidx.navigation.fragment.findNavController
import com.social.vidoza.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val splashViewModel: SplashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }
    private lateinit var splashFragmentBinding: SplashFragmentBinding;


    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
   splashFragmentBinding = SplashFragmentBinding.inflate(inflater,container,false)
        return  splashFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfUserIsAuthenticated()
    }


    private fun checkIfUserIsAuthenticated() {
     splashFragmentBinding.progressBar.visible()
        splashViewModel.checkIfUserIsAuthenticated()
        splashViewModel.authenticatedUserLiveData.observe(viewLifecycleOwner, { user ->
            when (user) {
                is ResponseState.Success -> {
                    if (user.data != null) {
                        user.data.uid?.let { getUserFromDatabase(it) }
                    } else {
                        goToAuthFragment()
                    }
                }

                is ResponseState.Error -> {
                    user.message?.let { context?.toast(it) }
                }
                is ResponseState.Loading -> {
                }
            }
        })
    }

    private fun getUserFromDatabase(uid: String) {
        splashViewModel.setUid(uid)
        splashViewModel.userLiveData.observe(viewLifecycleOwner, { user ->
            when (user) {
                is ResponseState.Success -> {
                    if (user.data != null)
                        goToMainActivity(user.data)
                }
                is ResponseState.Error -> {
                    user.message?.let { context?.toast(it) }
                }
                is ResponseState.Loading -> {
                }
            }
        })
    }

    private fun goToMainActivity(user: User) {

        val bundle = Bundle().apply {
            putSerializable(USERS_BUNDLE_OBJ, user)
        }
        CoroutinesHelper.delayWithMain(2000L) {
          splashFragmentBinding.progressBar.gone()
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment, bundle)
        }
    }

    private fun goToAuthFragment() {

        CoroutinesHelper.delayWithMain(2000L) {
          splashFragmentBinding.progressBar.gone()
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreen()
    }











}
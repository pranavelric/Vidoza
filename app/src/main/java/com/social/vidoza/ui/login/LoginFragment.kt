package com.social.vidoza.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.LoginFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.Constants.USERS_BUNDLE_OBJ
import com.social.vidoza.utils.gone
import com.social.vidoza.utils.toast
import com.social.vidoza.utils.toast_long
import com.social.vidoza.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class LoginFragment : Fragment() {


    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    private lateinit var loginFragmentBinding: LoginFragmentBinding;
    companion object {
        const val RC_SIGN_IN = 121
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginFragmentBinding = LoginFragmentBinding.inflate(inflater, container, false)
        return loginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        setClickListeners()
    }

    private fun getData() {

    }


    private fun setClickListeners() {



        loginFragmentBinding.layoutPhone.cirPhoneLoginButton.setOnClickListener {
            if (loginFragmentBinding.layoutPhone.editTextPhone.text.isNullOrBlank()) {
                loginFragmentBinding.layoutPhone.editTextPhone.error = "Please enter your phone number"
            } else {
                signInUsingPhoneNumber(loginFragmentBinding.layoutPhone.editTextPhone.text.toString())
                loginFragmentBinding.layoutPhone.root.gone()
                loginFragmentBinding.layoutOtp.root.visible()
            }
        }




    }


    private fun signInUsingPhoneNumber(phoneNumber: String) {

        loginFragmentBinding.layoutOtp.phonenumberText.text = "+91-${phoneNumber}"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    context?.toast_long("Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    context?.toast_long("SMS quota for project has been exceeded")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // Save verification ID and resending token so we can use them later
                val storedVerificationId = verificationId
                val resendToken = token
                loginFragmentBinding.layoutOtp.continueOtp.setOnClickListener {
                    val code =loginFragmentBinding.layoutOtp.pinView.text.toString()
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
        val auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity as MainActivity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        loginFragmentBinding.progressBar.visible()
        viewModel.signInWithPhoneNumber(credential)
        viewModel.authenticateUserLiveData.observe(viewLifecycleOwner, { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    loginFragmentBinding.progressBar.gone()
                    authenticatedUser.message?.let { context?.toast(it) }
                }
                is ResponseState.Success -> {
                    loginFragmentBinding.progressBar.gone()
                    if (authenticatedUser.data != null)

                        if (authenticatedUser.data.isNew == true) {
                            createNewUser(authenticatedUser.data)
                        } else {
                            updateUserIsNew(authenticatedUser.data,false)
                            goToMainFragment(authenticatedUser.data)
                        }
                         

                }
                is ResponseState.Loading -> {
                }
            }
        })

    }

    private fun updateUserIsNew(data: User, b: Boolean) {
   viewModel.updateUserIsNew(data,b)
    }


    private fun createNewUser(authenticatedUser: User?) {
        loginFragmentBinding.progressBar.visible()
        if (authenticatedUser != null) {
            Log.d("RRR", "createNewUser: call fun")
           viewModel.createUser(authenticatedUser)
        } else {
            loginFragmentBinding.progressBar.gone()
            context?.toast("Some error occured")
        }
       viewModel.createdUserLiveData.observe(viewLifecycleOwner, { user ->
           Log.d("RRR", "createNewUser: ${user}")

           when (user) {
                is ResponseState.Success -> {
                    Log.d("RRR", "createNewUser: dasdas")

                    loginFragmentBinding.progressBar.gone()
                    if (user.data != null) {
                        goToMainFragment(user.data)
                    }
                }
                is ResponseState.Error -> {
                    loginFragmentBinding.progressBar.gone()
                    user.message?.let { context?.toast(it) }

                }
                is ResponseState.Loading -> {

                }
            }


        })
    }


    private fun goToMainFragment(authenticatedUser: User?) {

        loginFragmentBinding.progressBar.visible()
        authenticatedUser?.uid?.let { viewModel.getUser(it) }
    viewModel.getUserLiveData.observe(viewLifecycleOwner, { userTask ->
            when (userTask) {
                is ResponseState.Success -> {

                    loginFragmentBinding.progressBar.gone()
                    val bundle = Bundle().apply {
                        putSerializable(USERS_BUNDLE_OBJ, userTask.data!!)
                    }

                    if(userTask.data?.name.isNullOrBlank()){
                        findNavController().navigate(R.id.action_loginFragment_to_profileFragment, bundle)
                    }else{
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundle)
                    }



                }
                is ResponseState.Error -> {
                    loginFragmentBinding.progressBar.gone()
                    context?.toast("Some Error occured")
                }
                is ResponseState.Loading -> {

                }
            }

        })


    }



































}
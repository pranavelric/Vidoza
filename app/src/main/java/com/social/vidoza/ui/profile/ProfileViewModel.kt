package com.social.vidoza.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.social.vidoza.data.model.User
import com.social.vidoza.data.repository.ProfileRepository
import com.social.vidoza.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository): ViewModel() {
    private var _uploadLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val uploadLiveData: LiveData<ResponseState<String>> get() = _uploadLiveData

    private var _updateLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val updateLiveData: LiveData<ResponseState<String>> get() = _updateLiveData


    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData


    fun uploadImageInFirebaseStorage(uid: String, uri: Uri)=viewModelScope.launch {
        _uploadLiveData.postValue(profileRepository.uploadImageToFirebaseStorage(uid, uri).value)
    }

    fun updateUserInfo(updateType: String, updateValue: String) =viewModelScope.launch{
        when (updateType) {
            Constants.USERNAME -> {
                _updateLiveData.postValue(profileRepository.updateUserUserName(updateValue).value)
            }
            Constants.USER_EMAIL -> {
                _updateLiveData.postValue(profileRepository.updateUserEmail(updateValue).value)
            }

            else -> {
                _updateLiveData.postValue(ResponseState.Error("Invalid update type"))
            }
        }
    }

    fun getUserFromDataBase(uid: String)=viewModelScope.launch {
        _userLiveData.postValue(profileRepository.getUser(uid).value)
    }

}
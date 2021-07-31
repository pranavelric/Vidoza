package com.social.vidoza.data.repository

import androidx.lifecycle.MutableLiveData
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vidoza.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivityRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val appRef: CollectionReference = rootRef.collection(Constants.APP)







    suspend fun checkForUpdate(versionCode: Int): MutableLiveData<ResponseState<Boolean>> {
        val gameMutableLiveData: MutableLiveData<ResponseState<Boolean>> =
            MutableLiveData()

        appRef.document("update").get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null && it.result!!.data?.isNotEmpty() == true) {

                    val update: Int = it.result!!["version"].toString().toInt()


                    if (versionCode == update) {
                        gameMutableLiveData.value = ResponseState.Success(false)
                    } else {
                        gameMutableLiveData.value = ResponseState.Success(true)
                    }
                } else {

                    gameMutableLiveData.value = ResponseState.Error("No update")
                }
            } else {

                gameMutableLiveData.value = ResponseState.Error("No update")
            }
        }.await()

        return gameMutableLiveData

    }



}
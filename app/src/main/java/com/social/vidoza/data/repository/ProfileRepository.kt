package com.social.vidoza.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.social.vidoza.data.model.User
import com.social.vidoza.utils.Constants.USERS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository  @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(USERS)
    private val storage = FirebaseStorage.getInstance()

  suspend  fun uploadImageToFirebaseStorage(
        uid: String,
        uri: Uri
    ): MutableLiveData<ResponseState<String>> {
        val uploadStatusLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
        val storageRef = storage.reference
        val uploadTask =
            storageRef.child("${uid}/profile_image/myprofileimage").putFile(uri)
      uploadTask.addOnProgressListener {
          val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
          Log.d("RRR", "Upload is $progress% done")
      }.addOnPausedListener {
            Log.d("RRR", "Upload is paused")
        }.addOnFailureListener {
            uploadStatusLiveData.value = it.message?.let { it1 -> ResponseState.Error(it1) }
        }.addOnSuccessListener { uploadTask ->

            storageRef.child("${uid}/profile_image/myprofileimage").downloadUrl.addOnSuccessListener { uri ->
                val file: MutableMap<String, Any> = HashMap()
                file["imageUrl"] = uri.toString()
                usersRef.document(uid).update(file).addOnCompleteListener { setTask ->
                    if (setTask.isSuccessful) {
                        uploadStatusLiveData.value =
                            ResponseState.Success("Image uploaded successfully")

                    } else {
                        uploadStatusLiveData.value =
                            ResponseState.Success("Image cannot be uploaded")

                    }

                }

            }

        }.await()


      if (uploadStatusLiveData.value !is ResponseState.Error) {
          uploadStatusLiveData.value = ResponseState.Success("Image uploaded successfully")
      }


      return uploadStatusLiveData

    }


suspend    fun updateUserEmail(newEmail: String): MutableLiveData<ResponseState<String>> {

        val updateUserLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()

        val file: MutableMap<String, Any> = HashMap()
        file["email"] =newEmail

        usersRef.document(firebaseAuth.currentUser!!.uid).update(file)
            .addOnCompleteListener { setTask ->
                if (setTask.isSuccessful) {

                    updateUserLiveData.value =   ResponseState.Success("Email updated successfully")
                } else {
                    updateUserLiveData.value =
                        setTask.exception?.message?.let { ResponseState.Error(it) }
                }
            }.await()


        return updateUserLiveData




    }



  suspend  fun updateUserPhoneNumber(newPhone: String): MutableLiveData<ResponseState<String>> {
        val updateUserLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()

        val file: MutableMap<String, Any> = HashMap()
        file["phoneNumber"] = newPhone

        usersRef.document(firebaseAuth.currentUser!!.uid).update(file)
            .addOnCompleteListener { setTask ->
                if (setTask.isSuccessful) {

                    updateUserLiveData.value =
                        ResponseState.Success("Phone number updated successfully")

                } else {
                    updateUserLiveData.value =
                        setTask.exception?.message?.let { ResponseState.Error(it) }
                }
            }.await()


        return updateUserLiveData
    }


   suspend fun updateUserUserName(newName: String): MutableLiveData<ResponseState<String>> {
        val updateUserLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()


        val file: MutableMap<String, Any> = HashMap()
        file["name"] = newName

        usersRef.document(firebaseAuth.currentUser!!.uid).update(file)
            .addOnCompleteListener { setTask ->
                if (setTask.isSuccessful) {
                    updateUserLiveData.value =
                        ResponseState.Success("Name updated successfully")
                } else {
                    updateUserLiveData.value =
                        setTask.exception?.message?.let { ResponseState.Error(it) }
                }
            }.await()

        return updateUserLiveData
    }


  suspend  fun getUser(uid: String): MutableLiveData<ResponseState<User>> {
        val userMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()

        usersRef.document(uid).get()
            .addOnCompleteListener { userTask: Task<DocumentSnapshot?> ->
                if (userTask.isSuccessful) {
                    val document = userTask.result
                    if (document!!.exists()) {
                        val user = document.toObject(User::class.java)
                        userMutableLiveData.value = ResponseState.Success(user!!)
                    } else {
                        userMutableLiveData.value =
                            ResponseState.Error("Some error occured!!")

                    }
                } else {
                    userMutableLiveData.value =
                        userTask.exception?.message?.let { ResponseState.Error(it) }


                }
            }.await()
        return userMutableLiveData
    }




}
package com.social.vidoza.data.repository

import androidx.lifecycle.MutableLiveData
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vidoza.data.model.User
import com.social.vidoza.utils.Constants.USERS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(){

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(USERS)

  suspend  fun firebaseSignInWithPhone(credential: PhoneAuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val profilePic = firebaseUser.photoUrl?.toString()
                    val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    authenticatedUserMutableLiveData.value =
                        ResponseState.Error("The verification code entered is invalid")
                }
            }
        }.await()
        return authenticatedUserMutableLiveData
    }

  fun createUserInFireStoreIfNotExist(authenticatedUser: User): MutableLiveData<ResponseState<User>> {


        val newUserMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
        val uidRef: DocumentReference? = authenticatedUser.uid?.let { usersRef.document(it) }

     uidRef?.get()?.addOnCompleteListener { uidTask ->
         if (uidTask.isSuccessful) {

             val document: DocumentSnapshot? = uidTask.result

             if (document != null) {
                 if (!document.exists()) {
                     authenticatedUser.isNew = authenticatedUser.isNew
                     uidRef.set(authenticatedUser)
                         .addOnCompleteListener { userCreationTask ->
                             if (userCreationTask.isSuccessful) {
                                 newUserMutableLiveData.value =
                                     ResponseState.Success(authenticatedUser)
                             } else {
                                 newUserMutableLiveData.value =
                                     userCreationTask.exception?.message?.let {
                                         ResponseState.Error(
                                             it
                                         )
                                     }
                             }
                         }

                 } else {
                     newUserMutableLiveData.value = ResponseState.Success(authenticatedUser)
                 }

             } else {
                 newUserMutableLiveData.value =
                     ResponseState.Error("Some error occured, Please try again later")
             }

         } else {
             newUserMutableLiveData.value = uidTask.exception?.message?.let {
                 ResponseState.Error(
                     it
                 )
             }
         }
     }

        return newUserMutableLiveData

    }

   suspend fun getUser(uid: String): MutableLiveData<ResponseState<User?>> {
       val userMutableLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
       usersRef.document(uid).get().addOnCompleteListener { userTask: Task<DocumentSnapshot?> ->
            if (userTask.isSuccessful) {
                val document = userTask.result
                if (document!!.exists()) {
                    val user = document.toObject(User::class.java)
                    userMutableLiveData.value = ResponseState.Success(user!!)
                } else {
                    userMutableLiveData.value = ResponseState.Error("Some error occured!!")

                }
            } else {
                userMutableLiveData.value =
                    userTask.exception?.message?.let { ResponseState.Error(it) }


            }
        }.await()
        return userMutableLiveData
    }



   fun checkIfUserIsAuthenticatedInFirebase(): MutableLiveData<ResponseState<User?>> {
        val isUserAuthenticatedLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            isUserAuthenticatedLiveData.value = ResponseState.Success(null)
        } else {
            val uid = firebaseUser.uid
            val name = firebaseUser.displayName
            val email = firebaseUser.email
            val phoneNumber = firebaseUser.phoneNumber
            val profilePic = firebaseUser.photoUrl?.toString()
            val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
            isUserAuthenticatedLiveData.value = ResponseState.Success(user)

        }
        return isUserAuthenticatedLiveData
    }

   suspend fun updateUserIsNew(id: User, b: Boolean) {
        val file: MutableMap<String, Any> = HashMap()
        file["new"] = false

       id.uid?.let {
           usersRef.document(it).update(file).addOnCompleteListener {

           }.await()
       }
    }

}
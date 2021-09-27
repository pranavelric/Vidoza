package com.social.vidoza.data.repository

import androidx.lifecycle.MutableLiveData
import com.gaming.earningvalleyadmin.utils.ResponseState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vidoza.data.model.User
import com.social.vidoza.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor() {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)

    suspend fun getUser(uid: String): MutableLiveData<ResponseState<User>> {
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


    suspend fun sendFCMTokenToDatabase(
        token: String,
        userId: String
    ): MutableLiveData<ResponseState<String>> {
        val userMutableLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()


        var file: HashMap<String, Any> = HashMap();
        file["fcm_token"] = token;

        usersRef.document(userId).update(file)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    userMutableLiveData.value =
                        ResponseState.Success("Fcm Token added into database successfully!")
                } else {
                    userMutableLiveData.value =
                        ResponseState.Error("Some error occured: ${it.exception?.message}")

                }
            }.await()
        return userMutableLiveData

    }

   suspend fun deleteFCMToken(uid: String):  MutableLiveData<ResponseState<String>> {
        val userMutableLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()



        val file: HashMap<String, Any> = HashMap();
        file["fcm_token"] = ""

        usersRef.document(uid).update(file).addOnCompleteListener {

                if (it.isSuccessful) {

                    userMutableLiveData.value =
                        ResponseState.Success("Fcm Token deleted from database successfully!")
                } else {

                    userMutableLiveData.value =
                        ResponseState.Error("Some error occured: ${it.exception?.message}")

                }
            }.await()
        return userMutableLiveData

    }






    val allRegisteredUserlist = ArrayList<User?>()
    suspend fun getUserss(userId: String): MutableLiveData<ResponseState<List<User?>>> {


        val gameMutableLiveData: MutableLiveData<ResponseState<List<User?>>> =
            MutableLiveData()

        allRegisteredUserlist.clear()
        usersRef.whereNotEqualTo("uid",userId).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(User::class.java)
                                allRegisteredUserlist.add(obj)
                        }


                        gameMutableLiveData.value = ResponseState.Success(allRegisteredUserlist)


                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No registered users found")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()


        return gameMutableLiveData
    }
















}
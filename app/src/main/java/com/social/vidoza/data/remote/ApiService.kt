package com.social.vidoza.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {

    @POST("send")
    fun sendRemoteMessages(
        @HeaderMap headers: HashMap<String, String>,
        @Body remoteBody: String
    ): Call<String>

}
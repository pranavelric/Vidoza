package com.social.vidoza.data.model

import java.io.Serializable

data class User(
    val uid: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    var imageUrl: String?,
    var fcm_token:String?="",
    var isNew:Boolean?=true

    ) : Serializable {
    constructor() : this("","","","",""){}
}



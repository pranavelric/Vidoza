package com.social.vidoza.data.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class User(
    val uid: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    var imageUrl: String?,

    ) : Serializable {
    constructor() : this("","","","",""){}
}



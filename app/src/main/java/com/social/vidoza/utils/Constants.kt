package com.social.vidoza.utils

object Constants {


    const val SHARED_PREFRENCE = "SHARED_PREFRENCES"
    const val NIGHT_MODE_ENABLED = "NIGHT_MODE_ENABLED"
    const val APP_THEME = "APP_THEME"

    const val BACKGROUND_IMAGE = "BACKGROUND_IMAGE"

    const val CHANNEL_ID = "CHANNEL_ID"
    const val CHANNEL_DESC = "CHANNEL_DESC"
    const val CHANNEL_NAME = "CHANNEL_NAME"
    const val NOTIFICATIONS = "NOTIFICATIONS"
    const val NOT_FOUND = "NOTFOUND"
    const val PERMISSION_CODE = 12345

    const val CALL_COMING= "CALL_COMING"


    // refrences name
    const val USERS = "USERS"
    const val USERS_BUNDLE_OBJ = "USER_BUNDLE_OBJ"
    const val CURRENT_USER = "CURRENT_USER"
    const val CALL_TYPE = "CALL_TYPE"


    //Users Info
    const val USERNAME = "USERNAME"
    const val USER_IMAGE_URL= "USER_IMAGE_URL"
    const val USER_EMAIL = "USEREMAIL"
    const val USER_PHONE_NUMBER = "USER_PHONE"
    const val APP = "APP";


    const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"

    const val REMOTE_MSG_TYPE = "type"
    const val REMOTE_MSG_INVITATION = "invitation"
    const val REMOTE_MSG_MEETING_TYPE = "meetingType"
    const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
    const val REMOTE_MSG_DATA = "data"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"


    const val REMOTE_MSG_INVITATION_RESPONSE= "invitationResponse"
    const val REMOTE_MSG_INVITATION_ACCEPTED = "accepted"
    const val REMOTE_MSG_INVITATION_REJECTED = "rejected"

    const val REMOTE_MSG_INVITATION_CANCELED= "cancelled"

    const val REMOTE_MSG_MEETING_ROOM = "meetingRoom"


    fun getHeaders(): HashMap<String, String> {
        val headers: HashMap<String, String> = HashMap()
        headers.put(
            REMOTE_MSG_AUTHORIZATION,
            "key=AAAAlPaklM8:APA91bFotFo_kQHY9LuX8WmYdE_Hrsx5cmmk85BmB7kVvJeGMeB8qwBSPgMwc4oFILEZwDTzRFzhVDu-tUTbezyBWktQWqk2asXXMcZZ7cOi_Ss_NXUJHydgPrnpcrq9ecCg_Ymn7I6M"
        )
        headers.put(REMOTE_MSG_CONTENT_TYPE, "application/json")
        return headers
    }


}
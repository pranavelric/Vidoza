package com.social.vidoza.utils


import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.thecode.aestheticdialogs.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


fun checkAboveOreo(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.O
}

fun checkAboveKitkat(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
}

fun checkAboveLollipop(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
}

fun isAboveR(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.R
}

fun isAboveP(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.P
}


fun Context.rateUs() {

    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("amzn://apps/android?p=$packageName")
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=$packageName")
            )
        )
    }

}


fun getTodaysDate(): String {
    return SimpleDateFormat.getDateInstance().format(Date())
}

fun getCurrentTime(): String {

    val date = Date(System.currentTimeMillis())
    val dateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    val time = dateFormat.format(date)
    return time

}


fun Activity.showCustomDialog(
    title: String,
    message: String,
    isCancelable: Boolean,
    dialogStyle: DialogStyle,
    dialogType: DialogType,
    dialogAnimation: DialogAnimation
) {


    AestheticDialog.Builder(this, dialogStyle, dialogType)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setDarkMode(false)
        .setGravity(Gravity.CENTER)
        .setAnimation(dialogAnimation)
        .setOnClickListener(object : OnDialogClickListener {
            override fun onClick(dialog: AestheticDialog.Builder) {
                dialog.dismiss()
            }
        })
        .show()

}


fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("", text))
}
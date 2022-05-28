@file:Suppress("unused")

package com.mankirat.common.lib.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MenuItem
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.mankirat.common.lib.R
import com.mankirat.common.lib.base.Base
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

open class CommonFun : Base("CommonFun") {

    fun getVersionName(context: Context): String {
        val version = "V " +
                try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (e: PackageManager.NameNotFoundException) {
                    //firebase event
                    "1.0.0" //BuildConfig.VERSION_NAME
                }

        return version
    }

    fun millisToTime(durationInMillis: Long): String {
        return when {
            (durationInMillis <= 0) -> ""
            (durationInMillis <= 59000) -> {
                val min = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
                val sec = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)
                String.format("%02d sec", (sec - TimeUnit.MINUTES.toSeconds(min)))
            }
            else -> {
                val min = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
                val sec = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)
                String.format("%02dm %02ds", min, (sec - TimeUnit.MINUTES.toSeconds(min)))
            }
        }
    }

    fun copyFile(context: Context, inputUri: Uri?, outputFile: File?, callback: ((success: Boolean) -> Unit)? = null) {
        log("copyFile: inputUri = $inputUri : outputFile = $outputFile")
        if (inputUri == null || outputFile == null) {
            callback?.invoke(false)
            return
        }

        context.contentResolver.openInputStream(inputUri).use { inputStream ->
            if (inputStream == null) {
                callback?.invoke(false)
                return
            }
            FileOutputStream(outputFile).use { outputStream ->
                val buffer = ByteArray(4096)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                callback?.invoke(true)
            }
        }
    }


    /*___________________________ Share Intent ___________________________*/

    fun shareLink(context: Context, audioLink: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(audioLink)
        context.startActivity(intent)
    }

    fun openWhatsapp(context: Context, number: String? = null, message: String? = null) {
        val packageManager = context.packageManager
        val isWhatsAppInstalled = try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        val isWhatsAppBusinessInstalled = try {
            packageManager.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        fun openWhatsapp(context: Context, business: Boolean, number: String?, message: String?) {
            val intent: Intent
            if (number != null) {
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$number")
            } else if (message != null) {
                intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, message)
                intent.type = "text/plain"
            } else {
                return
            }
            intent.setPackage(if (business) "com.whatsapp.w4b" else "com.whatsapp")
            context.startActivity(intent)
        }

        fun showDialog() {
            val dialog = AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle(context.getString(R.string.select))
                .setItems(R.array.whatsapp_options) { _, position ->
                    when (position) {
                        0 -> openWhatsapp(context, false, number, message)
                        1 -> openWhatsapp(context, true, number, message)
                    }
                    //dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
            dialog.show()
        }

        if (isWhatsAppInstalled && isWhatsAppBusinessInstalled) {
            showDialog()
        } else if (isWhatsAppInstalled) {
            openWhatsapp(context, false, number, message)
        } else if (isWhatsAppBusinessInstalled) {
            openWhatsapp(context, true, number, message)
        } else {
            toastShort(context, context.getString(R.string.whatsapp_not_installed))
        }

    }

    fun openTelegram(context: Context, userName: String? = null, message: String? = null) {
        val intent: Intent
        if (userName != null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://t.me/$userName")
        } else if (message != null) {
            intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
        } else {
            return
        }

        try {
            intent.setPackage("org.telegram.messenger")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toastShort(context, context.getString(R.string.telegram_not_installed))
        }
    }

    fun openMessage(context: Context, number: String? = null, message: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("smsto:${number ?: ""}")
        if (message != null) intent.putExtra("sms_body", message)
        context.startActivity(intent)
    }

    fun openEmail(context: Context, mail: Array<String>? = null, subject: String? = null, message: String? = null) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            if (mail != null) intent.putExtra(Intent.EXTRA_EMAIL, mail)
            if (subject != null) intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            if (message != null) intent.putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toastShort(context, context.getString(R.string.email_not_installed))
        }
    }

    /*___________________________ Date Time ___________________________*/
    fun timeStampToDate(millis: Long?, format: String = "dd-MMM-yyyy"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val netDate = Date(millis ?: System.currentTimeMillis())
        val formattedDate = sdf.format(netDate)
        log("timeStampToDate : millis = $millis : formattedDate = $formattedDate")
        return formattedDate
    }

    fun dateToTimeStamp(formattedDate: String, format: String): Long? {
        if (formattedDate.trim().isEmpty()) return null

        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val netDate = sdf.parse(formattedDate) ?: return null
        val timeStamp = Timestamp(netDate.time)
        val millis = timeStamp.time

        log("timeStampToDate : formattedDate = $formattedDate : millis = $millis")
        return millis
    }

    /*___________________________ Toolbar ___________________________*/
    fun setToolbarMoreIcon(toolbar: Toolbar, unWrappedDrawable: Drawable?, color: Int?) {
        if (unWrappedDrawable == null || color == null) {
            toolbar.overflowIcon = unWrappedDrawable
            return
        }

        val wrappedDrawable = DrawableCompat.wrap(unWrappedDrawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        toolbar.overflowIcon = wrappedDrawable
    }

    fun setToolbarMenuTint(itemView: MenuItem, color: Int) {
        val yourDrawable = itemView.icon
        yourDrawable?.mutate()
        yourDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            color,
            BlendModeCompat.SRC_IN
        )
    }

    fun getMimeType(file: File): String {
        val url = file.toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        var type: String? = null
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase(Locale.getDefault()))
        }
        return type ?: "image/*" // fallback type. You might set it to */*
    }

}
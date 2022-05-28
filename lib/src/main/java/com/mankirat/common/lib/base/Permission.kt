@file:Suppress("MemberVisibilityCanBePrivate")

package com.mankirat.common.lib.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mankirat.common.lib.R

class Permission(private val context: Context, private val permissionLauncher: ActivityResultLauncher<Array<String>>? = null, private val settingsLauncher: ActivityResultLauncher<Intent>? = null) {


    //@Inject
    //lateinit var firebaseEvent: FirebaseEvent

    private var permissionModel: PermissionModel? = null

    private fun invokePermissionCallback(status: Boolean) {
        val oldInstance = permissionModel
        permissionModel = null
        oldInstance?.callback?.invoke(status)
    }


    fun requestPermissions(permissions: Array<String>, dialogMsg: String, callback: (status: Boolean) -> Unit) {
        permissionModel = PermissionModel(permissions, dialogMsg, callback)
        if (isAllPermissions(permissions)) {
            invokePermissionCallback(true)
            //callback.invoke(true)
        } else {
            //permissionModel = PermissionModel(permissions, dialogMsg, callback)
            permissionLauncher?.launch(permissions)
        }
    }

    fun afterPermissionLauncherResponse(permissions: MutableMap<String, Boolean>) {
        when {
            isAllPermissions(permissions) -> {
                invokePermissionCallback(true)
            }
            shouldOpenPermissionSetting(permissions) -> {
                openSettingDialog(permissionModel?.dialogMsg)
            }
            else -> {
                invokePermissionCallback(false)
            }
        }
    }

    private fun openSettingDialog(msg: String?) {
        AlertDialog.Builder(context)
            .setMessage(msg ?: "Error")
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.settings)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                settingsLauncher?.launch(intent)
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.not_now)) { _, _ ->
                invokePermissionCallback(false)
            }
            .create()
            .show()
    }

    fun afterSettingLauncherResponse() {
        if (isAllPermissions(permissionModel?.permissions)) {
            invokePermissionCallback(true)
        } else {
            invokePermissionCallback(false)
        }
    }


    private fun shouldOpenPermissionSetting(permissionList: Map<String, Boolean>): Boolean {
        val activity: Activity = when (context) {
            is Activity -> context
            is Fragment -> context.requireActivity()
            else -> return false
        }

        permissionList.map {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, it.key)) {
                return false
            }
        }

        return true
    }

    private fun isAllPermissions(permissions: Array<String>?): Boolean {
        if (permissions == null) {
            //firebaseEvent.eventRare("Permission", "isAllPermissions")
            return false
        }

        permissions.map {
            if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun isAllPermissions(permissions: Map<String, Boolean>): Boolean {
        permissions.map {
            if (!it.value) return false
        }

        return true
    }


    //Display over other apps

    @SuppressLint("ObsoleteSdkInt")
    fun isOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true//5,4....
        else Settings.canDrawOverlays(context)//6,7,8....
    }

    fun requestOverlayPermission(dialogMsg: String, callback: (status: Boolean) -> Unit, overlayLauncher: ActivityResultLauncher<Intent>) {
        permissionModel = PermissionModel(arrayOf(), dialogMsg, callback)
        if (isOverlayPermission()) {
            invokePermissionCallback(true)
        } else {
            AlertDialog.Builder(context)
                .setMessage(dialogMsg)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.settings)) { dialog, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)//, Uri.parse("package:${activity.packageName}")
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    overlayLauncher.launch(intent)
                    dialog.dismiss()
                }
                .setNegativeButton(context.getString(R.string.not_now)) { _, _ ->
                    invokePermissionCallback(false)
                }
                .create()
                .show()
        }
    }

    fun afterOverlayLauncherResponse() {
        invokePermissionCallback(isOverlayPermission())
    }


    fun isUsageAccessPermission(): Boolean {
        return try {
            val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager = context.getSystemService(AppOpsManager::class.java)
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//10,11,12....
                appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            } else {//9,8,7.....
                @Suppress("DEPRECATION")
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            }
            (mode == AppOpsManager.MODE_ALLOWED)
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun requestUsageAccess(dialogMsg: String, callback: (status: Boolean) -> Unit, launcher: ActivityResultLauncher<Intent>) {
        permissionModel = PermissionModel(arrayOf(), dialogMsg, callback)
        if (isUsageAccessPermission()) {
            invokePermissionCallback(true)
        } else {
            AlertDialog.Builder(context)
                .setMessage(dialogMsg)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.settings)) { dialog, _ ->
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    launcher.launch(intent)
                    dialog.dismiss()
                }
                .setNegativeButton(context.getString(R.string.not_now)) { _, _ ->
                    invokePermissionCallback(false)
                }
                .create()
                .show()
        }
    }

    fun afterUsageAccessLauncherResponse() {
        invokePermissionCallback(isUsageAccessPermission())
    }

}

data class PermissionModel(
    val permissions: Array<String>,
    val dialogMsg: String,
    val callback: ((status: Boolean) -> Unit)
)

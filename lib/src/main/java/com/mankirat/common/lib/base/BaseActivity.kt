@file:Suppress("unused")

package com.mankirat.common.lib.base

import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mankirat.common.lib.R

abstract class BaseActivity(private var className: String) : AppCompatActivity() {

    fun log(msg: String?, e: Throwable? = null) {
        Log.e(className, msg, e)
        //ApplicationGlobal.instance.firebaseCrashlytics.log("E/$className: $msg")
    }

    fun toastShort(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun snackBarShort(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    private val mWindowManager by lazy { getSystemService(WindowManager::class.java) }
    private val progressBarView by lazy { layoutInflater.inflate(R.layout.layout_progress, null) }
    private val progressBarParams by lazy {
        //val xPosition = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, context.resources.displayMetrics).roundToInt()
        val width = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS//TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, resources.displayMetrics).roundToInt()
        val type = WindowManager.LayoutParams.TYPE_APPLICATION
        val flag = WindowManager.LayoutParams.FLAG_DIM_BEHIND

        WindowManager.LayoutParams(width, width, type, flag, PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.CENTER
            dimAmount = 0.5f
            //window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun showProgress() {
        if (!isDestroyed && !isFinishing) {
            hideProgress()
            mWindowManager.addView(progressBarView, progressBarParams)
        }
    }

    fun hideProgress() {
        try {
            mWindowManager.removeView(progressBarView)
        } catch (e: Exception) {

        }
    }


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permission?.afterPermissionLauncherResponse(permissions)
    }

    private val permissionSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permission?.afterSettingLauncherResponse()
    }


    private var permission: Permission? = null
    private fun getPermission(): Permission? {
        if (permission == null) permission = Permission(this, permissionLauncher, permissionSettingLauncher)
        return permission
    }

    fun permission(permissions: Array<String>, dialogMsg: String, callback: (status: Boolean) -> Unit) {
        getPermission()?.requestPermissions(permissions, dialogMsg, callback)
    }

    private val overlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permission?.afterOverlayLauncherResponse()
    }

    fun overlayPermission(dialogMsg: String, callback: (status: Boolean) -> Unit) {
        getPermission()?.requestOverlayPermission(dialogMsg, callback, overlayLauncher)
    }

    private val usageAccessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permission?.afterUsageAccessLauncherResponse()
    }

    fun usageAccessPermission(dialogMsg: String, callback: (status: Boolean) -> Unit) {
        getPermission()?.requestUsageAccess(dialogMsg, callback, usageAccessLauncher)
    }


}
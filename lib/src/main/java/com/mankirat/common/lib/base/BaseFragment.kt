package com.mankirat.common.lib.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<viewBinding : ViewBinding>(val className: String) : Fragment() {

    protected lateinit var binding: viewBinding
    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): viewBinding

    fun log(msg: String?, e: Throwable? = null) {
        Log.e(className, msg, e)
        //ApplicationGlobal.instance.firebaseCrashlytics.log("E/$className: $msg")
    }

    fun toastShort(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log("onCreateView")
        binding = getViewBinding(inflater, container)
        return binding.root
    }

    fun showProgress() {
        activity?.let {
            if (it is BaseActivity) it.showProgress()
        }
    }

    fun hideProgress() {
        activity?.let {
            if (it is BaseActivity) it.hideProgress()
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
        if (permission == null) permission = Permission(requireActivity(), permissionLauncher, permissionSettingLauncher)
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
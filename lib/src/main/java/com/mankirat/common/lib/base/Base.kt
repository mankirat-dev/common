package com.mankirat.common.lib.base

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class Base(private var className: String) {

    fun log(msg: String?, e: Throwable? = null) {
        Log.e(className, msg, e)
        //FirebaseCrashlytics.getInstance().log("E/$className: $msg")
    }

    fun toastShort(context: Context, msg: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toastShort(fragment: Fragment, msg: String?) {
        toastShort(fragment.requireContext(), msg)
    }
}
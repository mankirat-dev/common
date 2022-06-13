package com.mankirat.common.lib.util

import android.content.Context
import android.util.TypedValue
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

object Extensions : CommonFun()

fun Toolbar.setMoreIcon(drawableId: Int? = null, colorId: Int?) {
    val context = this.context
    val drawable = if (drawableId == null) overflowIcon else ContextCompat.getDrawable(context, drawableId)
    val color = if (colorId == null) null else ContextCompat.getColor(context, colorId)

    Extensions.setToolbarMoreIcon(this, drawable, color)
}

fun Toolbar.setMoreIconTint(colorId: Int) {
    val color = ContextCompat.getColor(this.context, colorId)

    Extensions.setToolbarMoreIcon(this, overflowIcon, color)
}

fun Toolbar.setMenuIconTint(colorId: Int) {
    val context = this.context
    val color = ContextCompat.getColor(context, colorId)

    this.menu.children.forEach { itemView ->
        Extensions.setToolbarMenuTint(itemView, color)
    }
}

fun NavHostFragment.getCurrentFragment(): Fragment? {
    val fragments = this.childFragmentManager.fragments
    return if (fragments.isNotEmpty()) {
        fragments[0]
    } else {
        null
    }
}

fun Float.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics).toInt()
}

fun Float.spToPx(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics).toInt()
}
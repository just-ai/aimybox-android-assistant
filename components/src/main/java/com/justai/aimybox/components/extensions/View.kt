package com.justai.aimybox.components.extensions

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.postDelayed


private const val RIPPLE_CLICK_DELAY = 150L

fun View.onTapRipple(action: (View) -> Unit) {
    setOnClickListener {
        handler.postDelayed(RIPPLE_CLICK_DELAY) { action(this) }
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attach: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attach)

fun Number.dpToPx(context: Context) =  toFloat() * context.resources.displayMetrics.density

fun Number.pxToDp(context: Context) = toFloat() / context.resources.displayMetrics.density
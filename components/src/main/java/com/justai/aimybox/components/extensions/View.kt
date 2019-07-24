package com.justai.aimybox.components.extensions

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

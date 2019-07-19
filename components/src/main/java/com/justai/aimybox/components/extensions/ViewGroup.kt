package com.justai.aimybox.components.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attach: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attach)

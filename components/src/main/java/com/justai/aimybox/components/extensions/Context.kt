package com.justai.aimybox.components.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.startActivityIfExist(intent: Intent) = (intent.resolveActivity(packageManager) != null)
    .also { isActivityExist -> if (isActivityExist) startActivity(intent) }
package com.justai.aimybox.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.justai.aimybox.components.AimyboxAssistantFragment
import com.justai.aimybox.components.extensions.isPermissionGranted

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            addAssistantFragment()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        grantResults.firstOrNull { it == PackageManager.PERMISSION_GRANTED }?.also {
            addAssistantFragment()
        }
    }

    private fun addAssistantFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.assistant_container, AimyboxAssistantFragment())
            commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        val assistantFragment = (supportFragmentManager.findFragmentById(R.id.assistant_container)
                as? AimyboxAssistantFragment)
        if (assistantFragment?.onBackPressed() != true) super.onBackPressed()
    }

}
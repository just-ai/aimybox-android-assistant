package com.justai.aimybox.assistant

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.justai.aimybox.components.AimyboxAssistantFragment
import kotlinx.coroutines.launch
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val assistantFragment = AimyboxAssistantFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.assistant_container, assistantFragment)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            lifecycleScope.launch {
                (application as AimyboxApplication).aimybox.sendRequest("Hello", sendRequestEvent = true).join()
            }
        }
    }

    override fun onBackPressed() {
        val assistantFragment = (supportFragmentManager.findFragmentById(R.id.assistant_container)
                as? AimyboxAssistantFragment)
        if (assistantFragment?.onBackPressed() != true) super.onBackPressed()
    }

}
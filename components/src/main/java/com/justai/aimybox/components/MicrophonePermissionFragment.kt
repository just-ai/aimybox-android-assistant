package com.justai.aimybox.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.extensions.onTapRipple

class MicrophonePermissionFragment : Fragment() {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 101
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return requireNotNull(container).inflate(R.layout.fragment_require_microphone_permission)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            findViewById<View>(R.id.fragment_require_microphone_permission_button_grant).onTapRipple {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
            }
            findViewById<View>(R.id.fragment_require_microphone_permission_button_cancel).onTapRipple {
                requireActivity().onBackPressed()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CODE
            && permissions.firstOrNull() == Manifest.permission.RECORD_AUDIO
            && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            requireActivity().onBackPressed()
        }
    }

}
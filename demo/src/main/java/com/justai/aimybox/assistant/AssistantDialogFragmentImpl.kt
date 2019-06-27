package com.justai.aimybox.assistant

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModelProviders
import com.justai.aimybox.components.AimyboxAssistantFragment

class AssistantDialogFragmentImpl
@RequiresPermission(android.Manifest.permission.RECORD_AUDIO) constructor(): AimyboxAssistantFragment() {
    override fun getAimyboxViewModel() =
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
}
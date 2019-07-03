package com.justai.aimybox.assistant

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModelProviders
import com.justai.aimybox.components.AimyboxAssistantFragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AssistantDialogFragmentImpl
@RequiresPermission(android.Manifest.permission.RECORD_AUDIO) constructor() : AimyboxAssistantFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: KodeinViewModelFactory by instance()

    override fun getAimyboxViewModel() =
        ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
}
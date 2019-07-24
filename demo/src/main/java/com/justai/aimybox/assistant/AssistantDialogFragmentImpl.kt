package com.justai.aimybox.assistant

import androidx.lifecycle.ViewModelProviders
import com.justai.aimybox.components.AimyboxAssistantFragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AssistantDialogFragmentImpl : AimyboxAssistantFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: KodeinViewModelFactory by instance()

    override fun getAimyboxViewModel() =
        ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
}
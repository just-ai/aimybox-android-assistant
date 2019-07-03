package com.justai.aimybox.assistant

import com.justai.aimybox.Aimybox
import com.justai.aimybox.components.AimyboxAssistantViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class MainViewModel(override val kodein: Kodein) : AimyboxAssistantViewModel(), KodeinViewModel {

    override val aimybox: Aimybox by instance()

    init {
        start()
    }
}
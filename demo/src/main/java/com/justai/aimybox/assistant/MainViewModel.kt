package com.justai.aimybox.assistant

import com.justai.aimybox.components.AimyboxAssistantViewModel
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.instance

class MainViewModel(override val kodein: Kodein) : AimyboxAssistantViewModel(kodein.direct.instance()), KodeinViewModel
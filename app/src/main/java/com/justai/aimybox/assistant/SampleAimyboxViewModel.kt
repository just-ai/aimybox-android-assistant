package com.justai.aimybox.assistant

import com.justai.aimybox.Aimybox
import com.justai.aimybox.components.AimyboxAssistantViewModel
import com.justai.aimybox.components.AimyboxProvider

/**
 * Sample ViewModel with aimybox instance. To receive aimybox instance into a constructor of the ViewModel,
 * you should use built-in [AimyboxAssistantViewModel.Factory] or create your own. The instance of the factory
 * is available via [AimyboxProvider].
 *
 * For example:
 * ```
 * val aimyboxProvider = (application as AimyboxApplication) // or this, if your Activity is AimyboxProvider
 * aimyboxViewModel = ViewModelProviders.of(this, aimyboxProvider.getViewModelFactory())
 *     .get(SampleAimyboxViewModel::class.java)
 * ```
 * */
class SampleAimyboxViewModel(aimybox: Aimybox) : AimyboxAssistantViewModel(aimybox) {
    // You can access aimybox instance here.
}
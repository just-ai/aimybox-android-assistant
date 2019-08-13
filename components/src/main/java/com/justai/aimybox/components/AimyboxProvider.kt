package com.justai.aimybox.components

import com.justai.aimybox.Aimybox

/**
 * Implement the interface in your Activity or Application to start using Aimybox components
 * */
interface AimyboxProvider {
    /**
     * Main class of the Aimybox library. You should have only one instance of the class for correct behavior.
     * */
    val aimybox: Aimybox
    /**
     *
     * */
    fun getViewModelFactory() = AimyboxAssistantViewModel.Factory.getInstance(aimybox)
}

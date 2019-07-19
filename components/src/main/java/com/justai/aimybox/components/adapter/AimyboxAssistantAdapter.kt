package com.justai.aimybox.components.adapter

import com.justai.aimybox.components.adapter.base.DelegatedAdapter
import com.justai.aimybox.components.widget.AssistantWidget
import kotlinx.coroutines.CoroutineScope

class AimyboxAssistantAdapter : DelegatedAdapter<AssistantWidget>(DELEGATES), CoroutineScope {
    companion object {
        private val DELEGATES = listOf(
            RecognitionDelegate
        )
    }
}
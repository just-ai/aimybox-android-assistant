package com.justai.aimybox.components.adapter

import com.justai.aimybox.components.base.DelegatedAdapter
import com.justai.aimybox.components.widget.AssistantWidget
import com.justai.aimybox.components.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AimyboxAssistantAdapter(
    onButtonClick: (Button) -> Unit
) : DelegatedAdapter<AssistantWidget>(), CoroutineScope {

    private var scrollJob: Job? = null

    override val delegates = listOf(
        RecognitionDelegate(::scrollRecyclerToBottom),
        SpeechDelegate(::scrollRecyclerToBottom),
        ImageDelegate(),
        ButtonsDelegate(onButtonClick)
    )

    override fun onDataSetChanged(data: List<AssistantWidget>) {
        scrollRecyclerToBottom()
    }

    private fun scrollRecyclerToBottom() {
        attachedRecycler?.let { recyclerView ->
            val itemCount = recyclerView.layoutManager?.itemCount ?: 0
            if (itemCount > 0) {
                scrollJob?.cancel()
                scrollJob = launch {
                    delay(100)
                    recyclerView.smoothScrollToPosition(itemCount - 1)
                }
            }
        }
    }
}
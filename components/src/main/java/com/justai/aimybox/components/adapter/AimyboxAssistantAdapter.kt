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
) : DelegatedAdapter<AssistantWidget>() {

    override val delegates = listOf(
        RecognitionDelegate,
        ResponseDelegate,
        RequestDelegate,
        ImageDelegate,
        ButtonsDelegate(onButtonClick)
    )

    override fun onDataSetChanged(data: List<AssistantWidget>) {
        scrollRecyclerToBottom()
    }

    private fun scrollRecyclerToBottom() {
        attachedRecycler?.let { recyclerView ->
            val itemCount = recyclerView.layoutManager?.itemCount ?: 0
            if (itemCount > 0) recyclerView.smoothScrollToPosition(itemCount - 1)
        }
    }
}
package com.justai.aimybox.components.adapter

import android.view.View
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.widgets.AssistantWidget
import com.justai.aimybox.components.widgets.SpeechWidget
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SpeechViewHolder(
    itemView: View,
    coroutineContext: CoroutineContext
) : AimyboxAssistantAdapter.ViewHolder(itemView, coroutineContext) {

    private var textView: TextView
    private var job: Job? = null

    init {
        itemView.apply {
            textView = findViewById(R.id.item_speech_text)
        }
    }

    override fun bind(widget: AssistantWidget) {
        require(widget is SpeechWidget)
        job?.cancel()

        textView.text = widget.currentText

        if (!widget.textChannel.isClosedForReceive) job = launch {
            widget.textChannel.consumeEach { newSpeech ->
                if (newSpeech.isNotBlank()) {
                    val newText = widget.currentText.takeIf(String::isNotBlank)?.let { it + '\n' }.orEmpty() + newSpeech
                    widget.currentText = newText
                    textView.text = newText
                }
            }
        }
    }
}
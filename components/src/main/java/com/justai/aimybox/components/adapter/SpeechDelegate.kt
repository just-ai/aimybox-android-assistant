package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.adapter.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.SpeechWidget
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SpeechDelegate(
    private val onUpdate: () -> Unit
) : AdapterDelegate<SpeechWidget, SpeechDelegate.ViewHolder>(SpeechWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_speech), onUpdate)
    }

    class ViewHolder(
        itemView: View,
        private val onUpdate: () -> Unit
    ) : AdapterDelegate.ViewHolder<SpeechWidget>(itemView) {

        private var textView: TextView = findViewById(R.id.item_speech_text)
        private var job: Job? = null

        override suspend fun bind(item: SpeechWidget) {
            job?.cancel()
            textView.text = item.currentText
            if (item.textChannel.isClosedForReceive) return

            coroutineScope {
                job = launch { item.observeUpdates() }
            }
        }

        private suspend fun SpeechWidget.observeUpdates() = textChannel.consumeEach { newSpeech ->
            if (newSpeech.isNotBlank()) {
                val oldText = currentText
                    ?.takeIf(String::isNotBlank)
                    ?.let { it + '\n' }
                    .orEmpty()
                val newText = (oldText + newSpeech).trim()
                currentText = newText
                textView.text = newText
                onUpdate()
            }
        }

    }
}
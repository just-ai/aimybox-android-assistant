package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.adapter.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.SpeechWidget
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object SpeechDelegate : AdapterDelegate<SpeechWidget, SpeechDelegate.ViewHolder>(SpeechWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_recognition))
    }

    class ViewHolder(itemView: View) : AdapterDelegate.ViewHolder<SpeechWidget>(itemView) {
        private var textView: TextView = findViewById(R.id.item_speech_text)
        private var job: Job? = null

        override suspend fun bind(item: SpeechWidget) {
            job?.cancel()
            textView.text = item.currentText
            coroutineScope {
                if (!item.textChannel.isClosedForReceive) job = launch {
                    item.textChannel.consumeEach { newSpeech ->
                        if (newSpeech.isNotBlank()) {
                            val newText =
                                item.currentText.takeIf(String::isNotBlank)?.let { it + '\n' }.orEmpty() + newSpeech
                            item.currentText = newText
                            textView.text = newText
                        }
                    }
                }
            }
        }
    }
}
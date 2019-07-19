package com.justai.aimybox.components.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.adapter.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.RecognitionWidget
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object RecognitionDelegate
    : AdapterDelegate<RecognitionWidget, RecognitionDelegate.ViewHolder>(RecognitionWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_recognition))
    }

    class ViewHolder(itemView: View) : AdapterDelegate.ViewHolder<RecognitionWidget>(itemView) {
        private var textView: TextView = findViewById(R.id.item_recognition_text)
        private var job: Job? = null

        private val textColor = getColor(R.color.text)
        private val hypotheticalTextColor = (textColor and 0x00FFFFFF) + 0x77000000 // add small transparency

        override suspend fun bind(item: RecognitionWidget) {
            coroutineScope {
                job?.cancel()
                textView.text = item.currentText
                if (!item.textChannel.isClosedForReceive) job = launch {
                    item.textChannel.consumeEach { newText ->
                        textView.text = if (item.textChannel.isClosedForSend) {
                            newText
                        } else {
                            createDifferenceSpannedString(item.currentText, newText) ?: newText
                        }
                        item.currentText = newText
                    }
                }
            }
        }

        private fun createDifferenceSpannedString(old: String, new: String): CharSequence? {
            var differenceIndex = 0
            if (old == new) return new

            for (i in 0 until new.length) {
                if (i >= old.length || new[i] != old[i]) {
                    differenceIndex = i
                    break
                }
            }

            if (differenceIndex == 0) return null

            val lastNotUpdatedWordEnd = new.substring(0..differenceIndex).lastIndexOf(' ')

            return buildSpan(new, lastNotUpdatedWordEnd)
        }

        private fun buildSpan(string: String, start: Int) = SpannableStringBuilder(string).apply {
            setSpan(
                ForegroundColorSpan(hypotheticalTextColor),
                start,
                string.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

    }

}
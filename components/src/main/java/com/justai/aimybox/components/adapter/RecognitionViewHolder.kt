package com.justai.aimybox.components.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.justai.aimybox.components.R
import com.justai.aimybox.components.widgets.AssistantWidget
import com.justai.aimybox.components.widgets.RecognitionWidget
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RecognitionViewHolder(
    itemView: View,
    coroutineContext: CoroutineContext
) : AimyboxAssistantAdapter.ViewHolder(itemView, coroutineContext) {

    private var textView: TextView
    private var job: Job? = null

    private val textColor = ContextCompat.getColor(itemView.context, R.color.text)
    private val hypotheticalTextColor = (textColor and 0x00FFFFFF) + 0x77000000 // add small transparency

    init {
        itemView.apply {
            textView = findViewById(R.id.item_recognition_text)
        }
    }

    override fun bind(widget: AssistantWidget) {
        require(widget is RecognitionWidget)
        job?.cancel()
        textView.text = widget.currentText
        if (!widget.textChannel.isClosedForReceive) job = launch {
            widget.textChannel.consumeEach { newText ->
                textView.text = if (widget.textChannel.isClosedForSend) {
                    newText
                } else {
                    createDifferenceSpannedString(widget.currentText, newText) ?: newText
                }
                widget.currentText = newText
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
        setSpan(ForegroundColorSpan(hypotheticalTextColor), start, string.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }

}
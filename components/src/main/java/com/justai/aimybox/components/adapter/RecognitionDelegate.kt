package com.justai.aimybox.components.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.RecognitionWidget
import kotlin.math.max

object RecognitionDelegate :
    AdapterDelegate<RecognitionWidget, RecognitionDelegate.ViewHolder>(RecognitionWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup) =
        ViewHolder(parent.inflate(R.layout.item_recognition))

    class ViewHolder(itemView: View) : AdapterDelegate.ViewHolder<RecognitionWidget>(itemView) {

        private val textColor = getColor(R.color.text)
        private val hypotheticalTextColor = (textColor and 0x00FFFFFF) + 0x77000000

        override fun bind(item: RecognitionWidget) {
            check(itemView is TextView)
            itemView.text = createDifferenceSpannedString(item.previousText.orEmpty(), item.text)
                ?: item.text
        }

        private fun createDifferenceSpannedString(old: String, new: String): CharSequence? {
            var differenceIndex = 0
            if (old == new) return new

            for (i in new.indices) if (i >= old.length || new[i] != old[i]) {
                differenceIndex = i
                break
            }

            if (differenceIndex == 0) return null

            val lastNotUpdatedWordEnd = max(new.substring(0, differenceIndex).lastIndexOf(' '), 0)

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
package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.ResponseWidget

object ResponseDelegate : AdapterDelegate<ResponseWidget, ResponseDelegate.ViewHolder>(ResponseWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_response))
    }

    class ViewHolder(
        itemView: View
    ) : AdapterDelegate.ViewHolder<ResponseWidget>(itemView) {

        private var textView: TextView = findViewById(R.id.item_speech_text)

        override suspend fun bind(item: ResponseWidget) {
            textView.text = item.text
        }

    }
}
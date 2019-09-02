package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.ResponseWidget

object ResponseDelegate :
    AdapterDelegate<ResponseWidget, ResponseDelegate.ViewHolder>(ResponseWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup) =
        ViewHolder(parent.inflate(R.layout.item_response))

    class ViewHolder(itemView: View) : AdapterDelegate.ViewHolder<ResponseWidget>(itemView) {

        override fun bind(item: ResponseWidget) {
            check(itemView is TextView)
            itemView.text = item.text
        }

    }
}
package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.RequestWidget

object RequestDelegate :
    AdapterDelegate<RequestWidget, RequestDelegate.ViewHolder>(RequestWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup) =
        ViewHolder(parent.inflate(R.layout.item_recognition))

    class ViewHolder(itemView: View) : AdapterDelegate.ViewHolder<RequestWidget>(itemView) {

        override fun bind(item: RequestWidget) {
            check(itemView is TextView)
            itemView.text = item.text
        }
    }

}
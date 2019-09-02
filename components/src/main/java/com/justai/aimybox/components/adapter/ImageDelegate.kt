package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.justai.aimybox.components.R
import com.justai.aimybox.components.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.ImageWidget

object ImageDelegate :
    AdapterDelegate<ImageWidget, ImageDelegate.ViewHolder>(ImageWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup) =
        ViewHolder(parent.inflate(R.layout.item_image))

    class ViewHolder(itemView: View) : AdapterDelegate.ViewHolder<ImageWidget>(itemView) {

        override fun bind(item: ImageWidget) {
            check(itemView is ImageView)
            Glide.with(context)
                .load(item.url)
                .into(itemView)
        }
    }
}
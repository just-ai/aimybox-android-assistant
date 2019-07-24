package com.justai.aimybox.components.adapter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.justai.aimybox.components.R
import com.justai.aimybox.components.widget.AssistantWidget
import com.justai.aimybox.components.widget.ImageWidget
import kotlin.coroutines.CoroutineContext
//
//class ImageViewHolder(
//    itemView: View,
//    coroutineContext: CoroutineContext
//) : AimyboxAssistantAdapter.ViewHolder(itemView, coroutineContext) {
//    override fun bind(widget: AssistantWidget) {
//        require(widget is ImageWidget && itemView is ImageView)
//        Glide.with(itemView)
//            .load(widget.source)
//            .placeholder(R.drawable.drawable_image_loading)
//            .into(itemView)
//    }
//}
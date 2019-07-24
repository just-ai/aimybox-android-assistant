package com.justai.aimybox.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.widget.AssistantWidget
import com.justai.aimybox.components.widget.ButtonsWidget
import kotlin.coroutines.CoroutineContext

//class ButtonsViewHolder(
//    itemView: View,
//    coroutineContext: CoroutineContext
//) : AimyboxAssistantAdapter.ViewHolder(itemView, coroutineContext) {
//    override fun bind(widget: AssistantWidget) {
//        require(widget is ButtonsWidget && itemView is LinearLayout)
//
//        //TODO implement views reuse
//        itemView.removeAllViews()
//
//        widget.buttons.map { button ->
//            LayoutInflater.from(itemView.context).inflate(R.layout.item_button, itemView, true).let { view ->
//                check(view is TextView)
//                view.text = button.text
//                view.setOnClickListener {
//                    //TODO Add button click listener
//                }
//            }
//        }
//    }
//}
package com.justai.aimybox.components.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justai.aimybox.components.R
import com.justai.aimybox.components.base.AdapterDelegate
import com.justai.aimybox.components.extensions.inflate
import com.justai.aimybox.components.widget.Button
import com.justai.aimybox.components.widget.ButtonsWidget

class ButtonsDelegate(
    private val onClick: (Button) -> Unit
) : AdapterDelegate<ButtonsWidget, ButtonsDelegate.ViewHolder>(ButtonsWidget::class.java) {

    override fun createViewHolder(parent: ViewGroup) =
        ViewHolder(parent.inflate(R.layout.item_buttons), onClick)

    class ViewHolder(
        itemView: View,
        private val onClick: (Button) -> Unit
    ) : AdapterDelegate.ViewHolder<ButtonsWidget>(itemView) {

        private val container = itemView as ViewGroup

        override fun bind(item: ButtonsWidget) {
            container.removeAllViews()
            item.buttons.map { button ->
                container.inflate(R.layout.item_button).apply {
                    check(this is TextView)
                    text = button.text
                    setOnClickListener { onClick(button) }
                    container.addView(this)
                }
            }
        }
    }
}
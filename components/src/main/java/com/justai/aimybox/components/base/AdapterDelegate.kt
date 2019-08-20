package com.justai.aimybox.components.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.justai.aimybox.components.R

abstract class AdapterDelegate<TItem, TViewHolder : AdapterDelegate.ViewHolder<TItem>>(
    private val itemClass: Class<TItem>
) {

    internal val viewType = ViewCompat.generateViewId()

    abstract fun createViewHolder(parent: ViewGroup): TViewHolder

    internal fun bind(viewHolder: TViewHolder, item: TItem) = viewHolder.bind(item)

    internal fun isFor(item: Any) = item::class.java == itemClass

    abstract class ViewHolder<TItem>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val context = checkNotNull(itemView.context)

        abstract fun bind(item: TItem)

        protected fun <T : View> findViewById(@IdRes id: Int) = itemView.findViewById<T>(id)!!

        protected fun getColor(@ColorRes id: Int) = ContextCompat.getColor(itemView.context, id)

    }

}
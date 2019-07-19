package com.justai.aimybox.components.adapter.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justai.aimybox.extensions.className
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class DelegatedAdapter<T>(
    private val delegates: List<AdapterDelegate<out T, *>>
) : RecyclerView.Adapter<AdapterDelegate.ViewHolder<T>>(), CoroutineScope {

    final override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private var data: List<T> = emptyList()

    override fun getItemCount() = data.size

    fun setData(newData: List<T>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]!!
        val delegate = delegates.find { it.isFor(item) }
        requireNotNull(delegate) { "AdapterDelegate is not registered for class ${item.className}" }
        return delegate.viewType
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDelegate.ViewHolder<T> {
        val delegate = requireNotNull(delegates.find { it.viewType == viewType })
        return delegate.createViewHolder(parent) as AdapterDelegate.ViewHolder<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: AdapterDelegate.ViewHolder<T>, position: Int) {
        val item = data[position]!!
        val delegate = requireNotNull(delegates.find { it.isFor(item) })
                as AdapterDelegate<T, AdapterDelegate.ViewHolder<T>>
        launch { delegate.bind(holder, item) }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        coroutineContext.cancelChildren()
    }

}


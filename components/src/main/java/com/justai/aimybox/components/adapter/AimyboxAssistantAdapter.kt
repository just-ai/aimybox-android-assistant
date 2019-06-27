package com.justai.aimybox.components.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justai.aimybox.components.L
import com.justai.aimybox.components.R
import com.justai.aimybox.components.widgets.AssistantWidget
import com.justai.aimybox.components.widgets.RecognitionWidget
import com.justai.aimybox.components.widgets.SpeechWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class AimyboxAssistantAdapter(
    private val context: Context
) : RecyclerView.Adapter<AimyboxAssistantAdapter.ViewHolder>(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main + Job()
    private var data: List<AssistantWidget> = emptyList()

    fun setData(newData: List<AssistantWidget>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            RecognitionWidget.VIEW_TYPE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_recognition, parent, false)
                RecognitionViewHolder(view, coroutineContext)
            }
            SpeechWidget.VIEW_TYPE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_speech, parent, false)
                SpeechViewHolder(view, coroutineContext)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemViewType(position: Int): Int = data[position].viewType

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        L.d("Binding $position")
        val widget = data[position]
        holder.bind(widget)
    }

    abstract class ViewHolder(
        itemView: View,
        override val coroutineContext: CoroutineContext
    ) : RecyclerView.ViewHolder(itemView), CoroutineScope {
        abstract fun bind(widget: AssistantWidget)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView.smoothScrollToPosition(getItemCount() - 1)
            }
        })
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        coroutineContext.cancel()
    }
}


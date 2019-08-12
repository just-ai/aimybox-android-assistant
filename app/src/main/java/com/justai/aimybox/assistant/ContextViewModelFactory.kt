package com.justai.aimybox.assistant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ContextViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        check(modelClass.constructors.size == 1)
        val constructor = modelClass.constructors.first()
        return constructor.newInstance(context) as T
    }
}
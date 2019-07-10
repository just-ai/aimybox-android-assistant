package com.justai.aimybox.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.Kodein

/**
 * The factory allows to create viewModels with a built-in [Kodein] instance
 * */
@Suppress("UNCHECKED_CAST")
class KodeinViewModelFactory(private val kodein: Kodein) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(KodeinViewModel::class.java.isAssignableFrom(modelClass)) { "$modelClass is not a subclass of KodeinViewModel" }
        require(modelClass.constructors.size == 1) { "KodeinViewModel must have only one constructor" }
        val constructor = checkNotNull(modelClass.constructors[0])
        return constructor.newInstance(kodein) as T
    }
}
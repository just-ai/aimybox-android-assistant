package com.justai.aimybox.assistant

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.singleton

class DemoApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        bind() from singleton { applicationContext }
        bind() from singleton { KodeinViewModelFactory(kodein) }
        importOnce(aimyboxModule)
    }
}
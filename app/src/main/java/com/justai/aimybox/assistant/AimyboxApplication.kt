package com.justai.aimybox.assistant

import android.app.Application
import android.content.Context
import com.justai.aimybox.Aimybox
import com.justai.aimybox.assistant.api.DummyDialogApi
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import com.justai.aimybox.speechkit.kaldi.KaldiAssets
import com.justai.aimybox.speechkit.kaldi.KaldiSpeechToText
import java.util.*

class AimyboxApplication : Application(), AimyboxProvider {

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {
        val textToSpeech = GooglePlatformTextToSpeech(context, Locale.getDefault())
        val speechToText = KaldiSpeechToText(KaldiAssets.fromApkAssets(this, "model/${Locale.getDefault().language}"))

        val dialogApi = DummyDialogApi()

        return Aimybox(Config.create(speechToText, textToSpeech, dialogApi))
    }
}
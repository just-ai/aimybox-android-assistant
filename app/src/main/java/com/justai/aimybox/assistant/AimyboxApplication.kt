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
import com.justai.aimybox.speechkit.kaldi.KaldiVoiceTrigger

class AimyboxApplication : Application(), AimyboxProvider {

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {
        val assets = KaldiAssets.fromApkAssets(this, "model")

        val textToSpeech = GooglePlatformTextToSpeech(context)
        val speechToText = KaldiSpeechToText(assets)
        val voiceTrigger = KaldiVoiceTrigger(assets, listOf("слушай"))

        val dialogApi = DummyDialogApi()

        return Aimybox(Config.create(speechToText, textToSpeech, dialogApi) {
            this.voiceTrigger = voiceTrigger
        })
    }
}
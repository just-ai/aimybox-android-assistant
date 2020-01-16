package com.justai.aimybox.assistant

import android.app.Application
import android.content.Context
import com.justai.aimybox.Aimybox
import com.justai.aimybox.assistant.api.DummyDialogApi
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import com.justai.aimybox.speechkit.pocketsphinx.*
import java.util.*

class AimyboxApplication : Application(), AimyboxProvider {

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {
        val assets = PocketsphinxAssets
            .fromApkAssets(
                context,
                acousticModelFileName = "model/${Locale.getDefault().language}",
                dictionaryFileName = "model/${Locale.getDefault().language}/dictionary.dict",
                grammarFileName = "model/${Locale.getDefault().language}/grammar.gram"
            )

        val provider = PocketsphinxRecognizerProvider(assets, keywordThreshold = 1e-40f)

        val textToSpeech = GooglePlatformTextToSpeech(context, Locale.getDefault())
        val speechToText = PocketsphinxSpeechToText(provider, assets.grammarFilePath!!)
        val voiceTrigger = PocketsphinxVoiceTrigger(provider, getString(R.string.keyphrase))
        val dialogApi = DummyDialogApi()

        return Aimybox(Config.create(speechToText, textToSpeech, dialogApi) {
            this.voiceTrigger = voiceTrigger
        })
    }
}
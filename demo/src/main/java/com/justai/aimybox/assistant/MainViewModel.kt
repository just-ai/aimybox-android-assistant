package com.justai.aimybox.assistant

import android.content.Context
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxDialogApi
import com.justai.aimybox.components.AimyboxAssistantViewModel
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.google.platform.GooglePlatformSpeechToText
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import java.util.*

class MainViewModel(context: Context) : AimyboxAssistantViewModel() {

    private val unitId = UUID.randomUUID().toString()

    private val textToSpeech = GooglePlatformTextToSpeech(context)
    private val speechToText = GooglePlatformSpeechToText(context)
    private val dialogApi = AimyboxDialogApi("VyjAgh9Oci3TznvSjX1pPMrRSztBBheA", unitId)

    private val config = Config.create(speechToText, textToSpeech, dialogApi)

    override val aimybox = Aimybox(config)

    init {
        start()
    }

}
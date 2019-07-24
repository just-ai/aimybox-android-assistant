package com.justai.aimybox.assistant

import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.DialogApi
import com.justai.aimybox.api.aimybox.AimyboxDialogApi
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.google.platform.GooglePlatformSpeechToText
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import com.justai.aimybox.speechtotext.SpeechToText
import com.justai.aimybox.texttospeech.TextToSpeech
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.util.*

val aimyboxModule = Kodein.Module("Aimybox") {

    val unitId = UUID.randomUUID().toString()

    bind<TextToSpeech>() with singleton { GooglePlatformTextToSpeech(instance()) }
    bind<SpeechToText>() with singleton { GooglePlatformSpeechToText(instance()) }
    bind<DialogApi>() with singleton { AimyboxDialogApi("VyjAgh9Oci3TznvSjX1pPMrRSztBBheA", unitId) }

    bind<Aimybox>() with singleton {
        Aimybox(Config.create(instance(), instance(), instance()))
    }

}
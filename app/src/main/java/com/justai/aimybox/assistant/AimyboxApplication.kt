package com.justai.aimybox.assistant

import android.app.Application
import android.content.Context
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxDialogApi
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.yandex.cloud.*
import java.util.*

class AimyboxApplication : Application(), AimyboxProvider {

    companion object {
        private const val AIMYBOX_API_KEY = "Ldf0j7WZi3KwNah2aNeXVIACz0lb9qMH"
        const val AIMYBOX_WEBHOOK_URL =
            "https://bot.jaicp.com/chatapi/webhook/zenbox/LNFjTAlh:67895092ac522776b15a126b7fcc1e775fd20b08"

        private const val YANDEX_TOKEN = ""
        private const val YANDEX_FOLDER_ID = ""
    }

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {


        val sttConfig =  YandexSpeechToText.Config(
            literatureText = true,
            rawResults = true,
            enableProfanityFilter = true,
        )

        val tokenGenerator = IAmTokenGenerator(YANDEX_TOKEN)

        val stt = YandexSpeechToText(
            iAmTokenProvider = tokenGenerator,
            folderId = YANDEX_FOLDER_ID,
            language = Language.RU,
            config = sttConfig
        )

        val ttsConfig = YandexTextToSpeech.ConfigV3(
            voice = Voice.FILIPP,
            speed = Speed(1.0f,)
        )

        val tts = YandexTextToSpeech.V3(
            context = context,
            iAmTokenProvider = tokenGenerator,
            folderId = YANDEX_FOLDER_ID,
            defaultLanguage = Language.RU,
            config = ttsConfig
        )

        val unitId = UUID.randomUUID().toString()

        val dialogApi = AimyboxDialogApi(
            apiKey = AIMYBOX_API_KEY,
            unitId = unitId,
            url = AIMYBOX_WEBHOOK_URL)

        val aimyboxConfig = Config.create(stt, tts, dialogApi)
        return Aimybox(aimyboxConfig, this)
    }
}
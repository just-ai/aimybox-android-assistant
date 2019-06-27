package com.justai.aimybox.components.widgets

import kotlinx.coroutines.channels.Channel

sealed class AssistantWidget(val viewType: Int)

data class RecognitionWidget(
    val textChannel: Channel<String> = Channel(8)
) : AssistantWidget(VIEW_TYPE) {
    var currentText = ""

    companion object {
        const val VIEW_TYPE = 1
    }
}

data class SpeechWidget(
    val textChannel: Channel<String> = Channel(8)
) : AssistantWidget(VIEW_TYPE) {
    var currentText = ""

    companion object {
        const val VIEW_TYPE = 2
    }
}

data class ImageWidget(val source: String) : AssistantWidget(VIEW_TYPE) {
    companion object {
        const val VIEW_TYPE = 3
    }
}

data class ButtonsWidget(val buttons: List<Button>) : AssistantWidget(VIEW_TYPE) {
    companion object {
        const val VIEW_TYPE = 4
    }
}
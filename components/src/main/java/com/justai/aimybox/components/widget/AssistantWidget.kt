package com.justai.aimybox.components.widget

import kotlinx.coroutines.channels.Channel

sealed class AssistantWidget()

data class RecognitionWidget(val textChannel: Channel<String> = Channel(8), var currentText: String = "") : AssistantWidget()

data class SpeechWidget(val textChannel: Channel<String> = Channel(8), var currentText: String? = null) : AssistantWidget()

data class ImageWidget(val source: String) : AssistantWidget()

data class ButtonsWidget(val buttons: List<Button>) : AssistantWidget()
package com.justai.aimybox.components.widget

sealed class AssistantWidget

data class RecognitionWidget(val text: String = "", val previousText: String? = null) : AssistantWidget()

data class RequestWidget(val text: String) : AssistantWidget()

data class ResponseWidget(val text: String) : AssistantWidget()

data class ImageWidget(val url: String) : AssistantWidget()

data class ButtonsWidget(val buttons: List<Button>) : AssistantWidget()
package com.justai.aimybox.components.widget

sealed class Button(val text: String)

class ResponseButton(text: String) : Button(text)

class LinkButton(text: String, val url: String): Button(text)

class PayloadButton(text: String, val payload: String): Button(text)
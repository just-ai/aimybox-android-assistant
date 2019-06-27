package com.justai.aimybox.components.widgets

sealed class Button(val text: String)

class ResponseButton(text: String) : Button(text)

class LinkButton(text: String, val url: String): Button(text)
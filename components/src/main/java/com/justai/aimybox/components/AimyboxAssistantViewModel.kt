package com.justai.aimybox.components

import androidx.annotation.CallSuper
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.DialogApi
import com.justai.aimybox.components.widget.*
import com.justai.aimybox.model.reply.ButtonsReply
import com.justai.aimybox.model.reply.ImageReply
import com.justai.aimybox.model.reply.Reply
import com.justai.aimybox.model.reply.TextReply
import com.justai.aimybox.speechtotext.SpeechToText
import com.justai.aimybox.voicetrigger.VoiceTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch

/**
 * Aimybox Fragment's view model.
 * */
open class AimyboxAssistantViewModel(val aimybox: Aimybox) : ViewModel(),
    CoroutineScope by MainScope() {

    private val isAssistantVisibleInternal = MutableLiveData<Boolean>()
    val isAssistantVisible = isAssistantVisibleInternal.immutable()

    private val widgetsInternal = MutableLiveData<List<AssistantWidget>>()
    val widgets = widgetsInternal.immutable()

    val aimyboxState = aimybox.stateChannel.toLiveData()

    private val soundVolumeRmsMutable = MutableLiveData<Float>()
    val soundVolumeRms: LiveData<Float> = soundVolumeRmsMutable

    private val urlIntentsInternal = Channel<String>()
    val urlIntents = urlIntentsInternal as ReceiveChannel<String>

    init {
        aimybox.stateChannel.observe { L.i(it) }
        aimybox.exceptions.observe { L.e(it) }

        val events = Channel<Any>(Channel.UNLIMITED)

        aimybox.speechToTextEvents.observe { events.send(it) }
        aimybox.dialogApiEvents.observe { events.send(it) }

        launch {
            events.consumeEach {
                when (it) {
                    is SpeechToText.Event -> onSpeechToTextEvent(it)
                    is DialogApi.Event -> onDialogApiEvent(it)
                    is VoiceTrigger.Event.Triggered -> isAssistantVisibleInternal.postValue(true)
                }
            }
        }
    }

    fun muteAimybox() = aimybox.mute()

    fun unmuteAimybox() = aimybox.unmute()

    fun setInitialPhrase(text: String) {
        widgetsInternal.value = listOf(ResponseWidget(text))
    }

    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun onButtonClick(button: Button) {
        removeButtonWidgets()
        when (button) {
            is ResponseButton -> aimybox.sendRequest(button.text)
            is PayloadButton -> aimybox.sendRequest(button.payload)
            is LinkButton -> urlIntentsInternal.safeOffer(button.url)
        }
    }

    fun onBackPressed() {
        isAssistantVisibleInternal.postValue(false)
        aimybox.standby()
    }

    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun onAssistantButtonClick() {
        if (isAssistantVisible.value != true) {
            isAssistantVisibleInternal.postValue(true)
        }
        aimybox.toggleRecognition()
    }

    private fun removeButtonWidgets() {
        widgetsInternal.value = widgetsInternal.value?.filter { it !is ButtonsWidget }.orEmpty()
    }

    private fun removeRecognitionWidgets(transform: List<AssistantWidget>.() -> List<AssistantWidget> = { this }) {
        widgetsInternal.value =
            widgetsInternal.value?.filter { it !is RecognitionWidget }.orEmpty().run(transform)
    }

    private fun addWidget(widget: AssistantWidget) {
        val currentList = widgets.value.orEmpty()
        widgetsInternal.value = currentList.plus(widget)
    }

    private fun onSpeechToTextEvent(event: SpeechToText.Event) {
        val previousText =
            (widgets.value?.find { it is RecognitionWidget } as? RecognitionWidget)?.text

        when (event) {
            is SpeechToText.Event.RecognitionStarted -> isAssistantVisibleInternal.postValue(true)
            is SpeechToText.Event.RecognitionPartialResult -> event.text
                ?.takeIf { it.isNotBlank() }
                ?.let { text ->
                    removeRecognitionWidgets { plus(RecognitionWidget(text, previousText)) }
                }
            is SpeechToText.Event.EmptyRecognitionResult,
            SpeechToText.Event.RecognitionCancelled -> removeRecognitionWidgets()
            is SpeechToText.Event.SoundVolumeRmsChanged -> {
                soundVolumeRmsMutable.postValue(event.rmsDb)
            }
        }
    }

    private fun onDialogApiEvent(event: DialogApi.Event) {
        when (event) {
            is DialogApi.Event.ResponseReceived -> {
                removeButtonWidgets()
                event.response.replies.forEach(::processReply)
            }
            is DialogApi.Event.RequestSent -> {
                removeRecognitionWidgets {
                    plus(RequestWidget(event.request.query))
                }
            }
        }
    }

    private fun processReply(reply: Reply) {
        L.d("Reply: $reply")
        when (reply) {
            is TextReply -> addWidget(ResponseWidget(reply.text))
            is ImageReply -> addWidget(ImageWidget(reply.url))
            is ButtonsReply -> {
                val buttons = reply.buttons.map { button ->
                    val url = button.url
                    val payload = button.payload
                    when {
                        url != null -> LinkButton(button.text, url)
                        payload != null -> PayloadButton(button.text, payload)
                        else -> ResponseButton(button.text)
                    }
                }
                addWidget(ButtonsWidget(buttons))
            }
        }
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    private fun <T> BroadcastChannel<T>.observe(action: suspend (T) -> Unit) {
        val channel = openSubscription()
        launch {
            channel.consumeEach { action(it) }
        }.invokeOnCompletion { channel.cancel() }
    }

    private fun <T> BroadcastChannel<T>.toLiveData(): LiveData<T> = MutableLiveData<T>().apply {
        observe { postValue(it) }
    }

    private fun <T> MutableLiveData<T>.immutable() = this as LiveData<T>

    private fun <T> SendChannel<T>.safeOffer(value: T) =
        takeUnless(SendChannel<T>::isClosedForSend)?.offer(value) ?: false

    class Factory private constructor(private val aimybox: Aimybox) : ViewModelProvider.Factory {

        companion object {
            private lateinit var instance: Factory
            fun getInstance(aimybox: Aimybox): Factory {
                if (!::instance.isInitialized) instance = Factory(aimybox)
                return instance
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            require(AimyboxAssistantViewModel::class.java.isAssignableFrom(modelClass)) { "$modelClass is not a subclass of AimyboxAssistantViewModel" }
            require(modelClass.constructors.size == 1) { "AimyboxAssistantViewModel must have only one constructor" }
            val constructor = checkNotNull(modelClass.constructors[0])
            return constructor.newInstance(aimybox) as T
        }
    }
}
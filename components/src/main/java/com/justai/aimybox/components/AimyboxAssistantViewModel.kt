package com.justai.aimybox.components

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.justai.aimybox.Aimybox
import com.justai.aimybox.components.widget.AssistantWidget
import com.justai.aimybox.components.widget.RecognitionWidget
import com.justai.aimybox.components.widget.SpeechWidget
import com.justai.aimybox.model.TextSpeech
import com.justai.aimybox.speechtotext.SpeechToText
import com.justai.aimybox.texttospeech.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

open class AimyboxAssistantViewModel(val aimybox: Aimybox) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    private val isAssistantVisibleInternal = MutableLiveData<Boolean>()
    val isAssistantVisible = isAssistantVisibleInternal.immutable()

    private val widgetsInternal = MutableLiveData<List<AssistantWidget>>()
    val widgets = widgetsInternal.immutable()

    val aimyboxState = aimybox.state.toLiveData()

    private val soundVolumeRmsMutable = MutableLiveData<Float>()
    val soundVolumeRms: LiveData<Float> = soundVolumeRmsMutable

    init {
        launch {
            aimybox.exceptions.observe(L::e)
        }

        val speechToText = aimybox.speechToTextEvents.openSubscription()
        val textToSpeech = aimybox.textToSpeechEvents.openSubscription()

        launch {
            while (isActive) select<Unit> {
                speechToText.onReceive { onSpeechToTextEvent(it) }
                textToSpeech.onReceive { onTextToSpeechEvent(it) }
            }
        }.invokeOnCompletion {
            listOf(textToSpeech, speechToText).forEach { it.cancel() }
        }
    }

    internal fun setAssistantVisibility(isVisible: Boolean) {
        isAssistantVisibleInternal.postValue(isVisible)
    }

    fun onButtonClick() {
        if (isAssistantVisible.value != true) {
            setAssistantVisibility(true)
        }
        aimybox.toggleRecognition()
    }

    private fun onSpeechToTextEvent(event: SpeechToText.Event) {
        val currentList = widgets.value
        val lastWidget = currentList?.findLast { it is RecognitionWidget } as? RecognitionWidget
        when (event) {
            is SpeechToText.Event.RecognitionStarted -> {
                widgetsInternal.postValue(currentList.orEmpty().plus(RecognitionWidget()))
            }
            is SpeechToText.Event.RecognitionPartialResult -> {
                lastWidget?.textChannel?.safeOffer(event.text?.capitalize().orEmpty())
            }
            is SpeechToText.Event.RecognitionResult -> {
                lastWidget?.textChannel?.safeOffer(event.text?.capitalize().orEmpty())
                lastWidget?.textChannel?.close()
            }
            is SpeechToText.Event.EmptyRecognitionResult, SpeechToText.Event.RecognitionCancelled -> {
                if (lastWidget is RecognitionWidget && !lastWidget.textChannel.isClosedForSend) {
                    widgetsInternal.postValue(currentList.dropLast(1))
                }
            }
            is SpeechToText.Event.SoundVolumeRmsChanged -> {
                soundVolumeRmsMutable.postValue(event.rmsDb)
                L.d("Sound volume ${event.rmsDb}")
            }
        }
    }

    private fun onTextToSpeechEvent(event: TextToSpeech.Event) {
        val currentList = widgets.value
        val lastWidget = currentList?.findLast { it is SpeechWidget } as SpeechWidget?
        when (event) {
            is TextToSpeech.Event.SpeechSequenceStarted -> {
                widgetsInternal.postValue(currentList.orEmpty().plus(SpeechWidget()))
            }
            is TextToSpeech.Event.SpeechStarted -> event.speech.let { speech ->
                when (speech) {
                    is TextSpeech -> lastWidget?.textChannel?.safeOffer(speech.text)
                    else -> Unit
                }
            }
            is TextToSpeech.Event.SpeechSequenceCompleted -> {
                lastWidget?.textChannel?.close()
            }
        }
    }


    @CallSuper
    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    private fun <T> BroadcastChannel<T>.observe(action: (T) -> Unit) {
        val channel = openSubscription()
        launch {
            channel.consumeEach(action)
        }.invokeOnCompletion { channel.cancel() }
    }

    private fun <T> BroadcastChannel<T>.toLiveData(): LiveData<T> = MutableLiveData<T>().apply {
        observe(::postValue)
    }

    private fun <T> MutableLiveData<T>.immutable() = this as LiveData<T>

    private fun <T> SendChannel<T>.safeOffer(value: T) =
        takeUnless(SendChannel<T>::isClosedForSend)?.offer(value) ?: false

}
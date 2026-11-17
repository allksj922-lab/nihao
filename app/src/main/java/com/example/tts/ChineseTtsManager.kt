package com.example.tts

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TtsState(
  val isReady: Boolean = false,
  val isChineseAvailable: Boolean = true,
  val activeUtteranceId: String? = null,
)

class ChineseTtsManager(private val context: Context) {

  private val _state = MutableStateFlow(TtsState())
  val state: StateFlow<TtsState> = _state.asStateFlow()

  private var tts: TextToSpeech? = null
  private var currentSequence: List<Pair<String, String>> = emptyList()
  private var currentIndex: Int = -1
  private var currentSpeed: Float = 1.0f

  init {
    tts =
      TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          val engine = tts ?: return@TextToSpeech
          val result =
            when {
              isLocaleSupported(engine, Locale.SIMPLIFIED_CHINESE) -> {
                engine.language = Locale.SIMPLIFIED_CHINESE
                true
              }
              isLocaleSupported(engine, Locale.CHINESE) -> {
                engine.language = Locale.CHINESE
                true
              }
              else -> false
            }

          engine.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
              override fun onStart(utteranceId: String?) {
                _state.value = _state.value.copy(activeUtteranceId = utteranceId)
              }

              override fun onDone(utteranceId: String?) {
                if (currentSequence.isNotEmpty() && currentIndex in currentSequence.indices) {
                  val nextIdx = currentIndex + 1
                  if (nextIdx < currentSequence.size) {
                    currentIndex = nextIdx
                    val (nextId, nextText) = currentSequence[nextIdx]
                    speakSingleInternal(nextText, nextId, currentSpeed, flush = false)
                    return
                  }
                }
                currentSequence = emptyList()
                currentIndex = -1
                _state.value = _state.value.copy(activeUtteranceId = null)
              }

              @Deprecated("Deprecated in Java")
              override fun onError(utteranceId: String?) {
                currentSequence = emptyList()
                currentIndex = -1
                _state.value = _state.value.copy(activeUtteranceId = null)
              }
            }
          )

          _state.value =
            _state.value.copy(isReady = true, isChineseAvailable = result)
        } else {
          _state.value = _state.value.copy(isReady = false, isChineseAvailable = false)
        }
      }
  }

  private fun isLocaleSupported(engine: TextToSpeech, locale: Locale): Boolean {
    val availability = engine.isLanguageAvailable(locale)
    return availability >= TextToSpeech.LANG_AVAILABLE
  }

  fun speak(text: String, utteranceId: String, speed: Float = 1.0f) {
    currentSequence = emptyList()
    currentIndex = -1
    currentSpeed = speed
    speakSingleInternal(text, utteranceId, speed, flush = true)
  }

  fun speakSequence(items: List<Pair<String, String>>, speed: Float = 1.0f) {
    if (items.isEmpty()) return
    currentSequence = items
    currentIndex = 0
    currentSpeed = speed
    val (firstId, firstText) = items.first()
    speakSingleInternal(firstText, firstId, speed, flush = true)
  }

  fun stop() {
    currentSequence = emptyList()
    currentIndex = -1
    tts?.stop()
    _state.value = _state.value.copy(activeUtteranceId = null)
  }

  private fun speakSingleInternal(
    text: String,
    utteranceId: String,
    speed: Float,
    flush: Boolean,
  ) {
    val engine = tts ?: return
    if (!_state.value.isReady) return
    engine.setSpeechRate(speed.coerceIn(0.5f, 1.5f))
    val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
    engine.speak(text, mode, null, utteranceId)
  }

  fun openTtsSettings() {
    val intent =
      Intent("com.android.settings.TTS_SETTINGS").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    runCatching { context.startActivity(intent) }
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}

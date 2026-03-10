package com.jodonnel.jobcoach.stt

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.os.Handler
import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "JobCoachSTT"

@Singleton
class GoogleSttEngine @Inject constructor(
    private val app: Application
) : SttEngine {

    private val _transcriptions = MutableSharedFlow<String>(extraBufferCapacity = 16)
    override val transcriptions: SharedFlow<String> = _transcriptions.asSharedFlow()

    private var recognizer: SpeechRecognizer? = null
    private var active = false
    private val mainHandler = Handler(Looper.getMainLooper())

    private val recognizerIntent: Intent
        get() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

    private val listener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val text = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
            Log.d(TAG, "onResults: $text")
            if (!text.isNullOrBlank()) {
                _transcriptions.tryEmit(text)
            }
            if (active) {
                mainHandler.post { recognizer?.startListening(recognizerIntent) }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val text = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
            Log.d(TAG, "onPartialResults: $text")
            if (!text.isNullOrBlank()) {
                _transcriptions.tryEmit("[partial] $text")
            }
        }

        override fun onError(error: Int) {
            Log.e(TAG, "onError: $error")
            if (active && error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                mainHandler.postDelayed({
                    recognizer?.startListening(recognizerIntent)
                }, 500)
            }
        }

        override fun onReadyForSpeech(params: Bundle?) { Log.d(TAG, "onReadyForSpeech") }
        override fun onBeginningOfSpeech() { Log.d(TAG, "onBeginningOfSpeech") }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { Log.d(TAG, "onEndOfSpeech") }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    override suspend fun start() {
        active = true
        Log.d(TAG, "start() — available: ${SpeechRecognizer.isRecognitionAvailable(app)}")
        mainHandler.post {
            if (recognizer == null) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(app).also {
                    it.setRecognitionListener(listener)
                }
            }
            Log.d(TAG, "startListening()")
            recognizer?.startListening(recognizerIntent)
        }
    }

    override suspend fun stop() {
        active = false
        mainHandler.post {
            recognizer?.stopListening()
            recognizer?.destroy()
            recognizer = null
        }
    }
}

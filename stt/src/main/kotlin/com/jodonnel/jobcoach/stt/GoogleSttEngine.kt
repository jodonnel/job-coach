package com.jodonnel.jobcoach.stt

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Speech-to-Text engine.
 * Uses Android's built-in SpeechRecognizer for V1.
 * Fast path to working STT without bundling a model.
 *
 * TODO: Wire up SpeechRecognizer with BT audio source from glasses mic.
 */
@Singleton
class GoogleSttEngine @Inject constructor() : SttEngine {

    private val _transcriptions = MutableSharedFlow<String>()
    override val transcriptions: SharedFlow<String> = _transcriptions.asSharedFlow()

    private var active = false

    override suspend fun start() {
        active = true
        // TODO: Initialize SpeechRecognizer
        // Set audio source to BT SCO (glasses mic)
        // recognizer.startListening(intent)
    }

    override suspend fun stop() {
        active = false
        // TODO: recognizer.stopListening()
    }
}

package com.jodonnel.jobcoach.stt

import kotlinx.coroutines.flow.SharedFlow

/**
 * Speech-to-text engine interface.
 * Implementations: GoogleSttEngine (cloud), WhisperEngine (on-device).
 */
interface SttEngine {
    val transcriptions: SharedFlow<String>
    suspend fun start()
    suspend fun stop()
}

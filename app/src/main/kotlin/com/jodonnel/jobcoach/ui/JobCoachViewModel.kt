package com.jodonnel.jobcoach.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jodonnel.jobcoach.core.network.OpenShiftClient
import com.jodonnel.jobcoach.stt.SttEngine
import com.jodonnel.jobcoach.wearable.DatManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobCoachUiState(
    val glassesConnected: Boolean = false,
    val listening: Boolean = false,
    val profileName: String = "Default",
    val lastTranscription: String = "",
    val lastEvent: String = ""
)

@HiltViewModel
class JobCoachViewModel @Inject constructor(
    private val datManager: DatManager,
    private val sttEngine: SttEngine,
    private val openShiftClient: OpenShiftClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobCoachUiState())
    val uiState: StateFlow<JobCoachUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            datManager.connectionState.collect { connected ->
                _uiState.update { it.copy(glassesConnected = connected) }
            }
        }

        viewModelScope.launch {
            sttEngine.transcriptions.collect { text ->
                _uiState.update { it.copy(lastTranscription = text) }
                // Send transcription as CloudEvent to ROSA
                sendEvent(text)
            }
        }
    }

    fun toggleConnection() {
        viewModelScope.launch {
            if (_uiState.value.glassesConnected) {
                datManager.disconnect()
            } else {
                datManager.connect()
            }
        }
    }

    fun toggleListening() {
        viewModelScope.launch {
            if (_uiState.value.listening) {
                sttEngine.stop()
                _uiState.update { it.copy(listening = false) }
            } else {
                sttEngine.start()
                _uiState.update { it.copy(listening = true) }
            }
        }
    }

    private fun sendEvent(transcription: String) {
        viewModelScope.launch {
            val result = openShiftClient.sendCloudEvent(
                type = "ohc.demo.job-coach.voice",
                source = "meta-glasses://local",
                data = mapOf(
                    "transcription" to transcription,
                    "profile" to _uiState.value.profileName
                )
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(lastEvent = "Sent: $transcription") }
            }
        }
    }
}

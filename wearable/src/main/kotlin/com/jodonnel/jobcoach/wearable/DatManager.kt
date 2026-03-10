package com.jodonnel.jobcoach.wearable

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the Meta Wearables DAT SDK connection lifecycle.
 *
 * V1: Wraps DAT connection/disconnection and exposes connection state.
 * Audio capture from the glasses mic array flows through here.
 *
 * TODO: Wire up actual DAT SDK calls once we verify the SDK auth works.
 * For now, this is a functional skeleton that the ViewModel can bind to.
 */
@Singleton
class DatManager @Inject constructor() {

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    private val _audioAvailable = MutableStateFlow(false)
    val audioAvailable: StateFlow<Boolean> = _audioAvailable.asStateFlow()

    suspend fun connect() {
        // TODO: Initialize DAT SDK session
        // val session = MwDat.startSession(context)
        // session.connect()
        _connectionState.value = true
        _audioAvailable.value = true
    }

    suspend fun disconnect() {
        // TODO: Tear down DAT SDK session
        _audioAvailable.value = false
        _connectionState.value = false
    }
}

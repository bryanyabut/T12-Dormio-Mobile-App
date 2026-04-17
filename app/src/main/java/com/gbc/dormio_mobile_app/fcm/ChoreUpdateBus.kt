package com.gbc.dormio_mobile_app.fcm

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ChoreUpdateBus {
    private val _updatesFlow = MutableSharedFlow<ChoreUpdateEvent>()
    val updatesFlow = _updatesFlow.asSharedFlow()

    suspend fun post(choreId: String, status: String) {
        _updatesFlow.emit(ChoreUpdateEvent(choreId, status))
    }
}

data class ChoreUpdateEvent(
    val choreId: String,
    val status: String
)
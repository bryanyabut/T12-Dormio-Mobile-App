package com.gbc.dormio_mobile_app.fcm

import com.gbc.dormio_mobile_app.data.model.RequestStatus
import com.gbc.dormio_mobile_app.data.model.UserDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object MaintenanceUpdateBus {
    private val _updatesFlow = MutableSharedFlow<Triple<String, RequestStatus, UserDto>>(extraBufferCapacity = 10)
    val updatesFlow = _updatesFlow.asSharedFlow()

    fun post(requestId: String, status: RequestStatus, user: UserDto) {
        _updatesFlow.tryEmit(Triple(requestId, status, user))
    }
}
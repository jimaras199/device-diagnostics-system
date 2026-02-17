package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.model.EventDto

interface EventsRepository {
    suspend fun getEvents(deviceId: Int): List<EventDto>
}

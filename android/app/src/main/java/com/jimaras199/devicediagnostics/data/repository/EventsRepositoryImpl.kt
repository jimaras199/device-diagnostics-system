package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.EventsApi
import com.jimaras199.devicediagnostics.data.model.EventDto

class EventsRepositoryImpl(private val api: EventsApi) : EventsRepository {
    override suspend fun getEvents(deviceId: Int): List<EventDto> =
        api.getEvents(deviceId)
}

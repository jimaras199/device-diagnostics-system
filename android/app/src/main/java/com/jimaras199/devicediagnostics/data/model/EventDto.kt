package com.jimaras199.devicediagnostics.data.model

data class EventDto(
    val id: Int,
    val level: String,
    val message: String,
    val timestampUtc: String
)

package com.jimaras199.devicediagnostics.data.model

data class DeviceDto(
    val id: Int,
    val name: String,
    val model: String?,
    val lastSeen: String
)

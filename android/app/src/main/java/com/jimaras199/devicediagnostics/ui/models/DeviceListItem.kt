package com.jimaras199.devicediagnostics.ui.models

data class DeviceListItem(
    val id: Int,
    val name: String,
    val model: String?,
    val lastSeenUtc: String
)

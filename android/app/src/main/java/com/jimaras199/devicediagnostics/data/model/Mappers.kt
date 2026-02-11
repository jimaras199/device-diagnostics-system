package com.jimaras199.devicediagnostics.data.model

import com.jimaras199.devicediagnostics.ui.models.DeviceListItem

fun DeviceDashboardDto.toDeviceListItem(): DeviceListItem =
    DeviceListItem(
        id = id,
        name = name,
        model = model,
        lastSeenUtc = lastSeenUtc
    )

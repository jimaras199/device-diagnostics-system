package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.DemoSeedResponseDto
import com.jimaras199.devicediagnostics.data.api.DemoStatusDto

interface   DemoRepository {
    suspend fun seedDemo(): DemoSeedResponseDto
    suspend fun getStatus(): DemoStatusDto
}

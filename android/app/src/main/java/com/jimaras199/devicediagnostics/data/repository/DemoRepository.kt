package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.DemoSeedResponseDto

interface DemoRepository {
    suspend fun seedDemo(): DemoSeedResponseDto
}

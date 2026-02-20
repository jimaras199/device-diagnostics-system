package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.DemoApi
import com.jimaras199.devicediagnostics.data.api.DemoSeedResponseDto

class DemoRepositoryImpl(
    private val api: DemoApi
) : DemoRepository {
    override suspend fun seedDemo(): DemoSeedResponseDto = api.seedDemo()
}

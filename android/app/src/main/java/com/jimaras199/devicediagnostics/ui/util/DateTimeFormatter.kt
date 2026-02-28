package com.jimaras199.devicediagnostics.ui.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val outputFormatter =
    DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.ENGLISH)

fun formatUtcTimestamp(utc: String): String {
    return try {
        OffsetDateTime.parse(utc).format(outputFormatter)
    } catch (_: Exception) {
        try {
            LocalDateTime.parse(utc)
                .atOffset(ZoneOffset.UTC)
                .format(outputFormatter)
        } catch (_: Exception) {
            utc
        }
    }
}
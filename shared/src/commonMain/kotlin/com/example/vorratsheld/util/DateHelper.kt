package com.example.vorratsheld.util

import com.example.vorratsheld.data.enums.ExpiryUrgency
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

// Datum und Ablauf rund ums MHD
object DateHelper {
    private const val DAY_MS = 24L * 60 * 60 * 1000

    // Aktuelle Zeit in ms, System.currentTimeMillis() gibt es auf iOS nicht
    @OptIn(ExperimentalTime::class)
    fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

    // Der DatePicker liefert Mitternacht in UTC, daraus wird der Kalendertag
    fun expiryDateToLocalDate(expiryDate: Long): LocalDate =
        LocalDate.fromEpochDays(expiryDate.floorDiv(DAY_MS))

    @OptIn(ExperimentalTime::class)
    fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // Tage bis zum Ablaufdatum (negativ = abgelaufen), verglichen werden Kalendertage
    fun daysUntilExpiry(expiryDate: Long): Long {
        return expiryDateToLocalDate(expiryDate).toEpochDays() - today().toEpochDays()
    }

    fun expiryUrgencyFor(expiryDate: Long): ExpiryUrgency {
        val days = daysUntilExpiry(expiryDate)
        return when {
            days < 0 -> ExpiryUrgency.ABGELAUFEN
            days <= 3 -> ExpiryUrgency.BALD_ABLAUFEND
            days <= 10 -> ExpiryUrgency.IN_KUERZE
            else -> ExpiryUrgency.UNKRITISCH
        }
    }

    // z. B. 04.10.2026
    fun formatDate(dateMillis: Long): String {
        val date = expiryDateToLocalDate(dateMillis)
        val day = date.day.toString().padStart(2, '0')
        val month = date.month.number.toString().padStart(2, '0')
        return "$day.$month.${date.year}"
    }

    // Lokales Datum für echte Zeitpunkte (das MHD selbst ist schon ein Kalendertag)
    @OptIn(ExperimentalTime::class)
    fun formatLocalDate(millis: Long): String {
        val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val day = date.day.toString().padStart(2, '0')
        val month = date.month.number.toString().padStart(2, '0')
        return "$day.$month.${date.year}"
    }
}

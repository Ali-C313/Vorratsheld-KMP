package com.example.vorratsheld.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

// Sucht im erkannten Verpackungstext das Ablaufdatum (MHD)
object ExpiryDateParser {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    // Wörter, hinter denen meistens das MHD steht
    private val expiryKeywords = listOf(
        "MHD", "MINDESTENS", "HALTBAR", "VERBRAUCH", "ZU VERBR",
        "BEST BEFORE", "BEST BY", "BB", "BBE", "USE BY", "EXP",
        "CONSOMMER", "DLC", "DDM", "CONSUMARSI", "SCAD", "CONSUMIR", "CAD",
        "NAJLEPIEJ", "SPOŻYĆ", "SKT", "TETT", "ГОДЕН", "ГОДНО", "ВЖИТИ"
    )

    // Herstell- und Packdatum sollen nicht als MHD durchgehen
    private val productionKeywords = listOf("HERG", "PROD", "PACK", "FABR", "ИЗГОТ", "ВИГОТ")

    private val months = mapOf(
        "JAN" to 1, "FEB" to 2, "MÄR" to 3, "MAR" to 3, "MRZ" to 3, "APR" to 4,
        "MAI" to 5, "MAY" to 5, "JUN" to 6, "JUL" to 7, "AUG" to 8, "SEP" to 9,
        "OKT" to 10, "OCT" to 10, "NOV" to 11, "DEZ" to 12, "DEC" to 12
    )

    private val dayMonthYear = Regex("""(?<!\d)(\d{1,2}) ?[./\-] ?(\d{1,2}) ?[./\-] ?(\d{4}|\d{2})(?!\d)""")
    private val spacedDayMonthYear = Regex("""(?<!\d)(\d{2}) (\d{2}) (\d{4}|\d{2})(?!\d)""")
    private val yearMonthDay = Regex("""(?<!\d)(\d{4})[./\-](\d{1,2})[./\-](\d{1,2})(?!\d)""")
    private val monthYear = Regex("""(?<![\d./\-])(\d{1,2}) ?[./\-] ?(\d{4})(?!\d)""")
    private val shortMonthYear = Regex("""(?<![\d./\-])(\d{2}) ?[./] ?(\d{2})(?![\d./\-])""")
    private val monthName = Regex(
        """(?<!\d)(?:(\d{1,2})\.? ?)?(JAN|FEB|MÄR|MAR|MRZ|APR|MAI|MAY|JUN|JUL|AUG|SEP|OKT|OCT|NOV|DEZ|DEC)[A-ZÄ]*\.? ?(\d{4}|\d{2})(?!\d)"""
    )

    // Typische Lesefehler der Texterkennung neben Ziffern
    private val lookalikes = mapOf('O' to '0', 'Q' to '0', 'I' to '1', 'L' to '1', '|' to '1', 'S' to '5', 'B' to '8')

    private class Candidate(val date: LocalDate, val score: Int)

    fun parse(text: String): LocalDate? {
        val clean = normalize(text)
        val candidates = mutableListOf<Candidate>()

        fun add(match: MatchResult, date: LocalDate?, baseScore: Int, needsKeyword: Boolean = false) {
            if (date == null || !isPlausible(date)) return
            val before = clean.substring((match.range.first - 30).coerceAtLeast(0), match.range.first)
            // Das nächstgelegene Stichwort zählt
            val expiryAt = expiryKeywords.maxOf { before.lastIndexOf(it) }
            val productionAt = productionKeywords.maxOf { before.lastIndexOf(it) }
            val hasKeyword = expiryAt > productionAt
            if (needsKeyword && !hasKeyword) return
            var score = baseScore
            if (hasKeyword) score += 5
            if (productionAt > expiryAt) score -= 4
            candidates += Candidate(date, score)
        }

        dayMonthYear.findAll(clean).forEach { m ->
            val (d, mo, y) = m.destructured
            add(m, dateOf(year(y), mo.toInt(), d.toInt()), 3)
        }
        spacedDayMonthYear.findAll(clean).forEach { m ->
            val (d, mo, y) = m.destructured
            add(m, dateOf(year(y), mo.toInt(), d.toInt()), 2)
        }
        yearMonthDay.findAll(clean).forEach { m ->
            val (y, mo, d) = m.destructured
            add(m, dateOf(y.toInt(), mo.toInt(), d.toInt()), 3)
        }
        monthYear.findAll(clean).forEach { m ->
            val (mo, y) = m.destructured
            add(m, endOfMonth(y.toInt(), mo.toInt()), 2)
        }
        shortMonthYear.findAll(clean).forEach { m ->
            val (mo, y) = m.destructured
            add(m, endOfMonth(year(y), mo.toInt()), 1, needsKeyword = true)
        }
        monthName.findAll(clean).forEach { m ->
            val day = m.groupValues[1]
            val month = months.getValue(m.groupValues[2])
            val y = year(m.groupValues[3])
            val date = if (day.isEmpty()) endOfMonth(y, month) else dateOf(y, month, day.toInt())
            add(m, date, 3)
        }

        // Bei Gleichstand das spätere Datum, das frühere ist oft das Packdatum
        return candidates
            .maxWithOrNull(compareBy<Candidate> { it.score }.thenBy { it.date })
            ?.date
    }

    fun toMillis(date: LocalDate): Long = date.toEpochDays() * DAY_MS

    private fun normalize(text: String): String {
        val upper = text.uppercase()
        val chars = upper.toCharArray()
        for (i in chars.indices) {
            val replacement = lookalikes[chars[i]] ?: continue
            val prev = upper.getOrNull(i - 1)
            val next = upper.getOrNull(i + 1)
            val nearNumber = prev.isNumberPart() || next.isNumberPart()
            if (nearNumber && prev?.isLetter() != true && next?.isLetter() != true) {
                chars[i] = replacement
            }
        }
        return chars.concatToString()
    }

    private fun Char?.isNumberPart() = this != null && (isDigit() || this in "./-")

    private fun year(raw: String): Int = if (raw.length == 2) 2000 + raw.toInt() else raw.toInt()

    private fun dateOf(year: Int, month: Int, day: Int): LocalDate? {
        if (month !in 1..12 || day !in 1..31) return null
        return runCatching { LocalDate(year, month, day) }.getOrNull()
    }

    private fun endOfMonth(year: Int, month: Int): LocalDate? {
        val first = dateOf(year, month, 1) ?: return null
        return first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
    }

    // Abgelaufen bis vor einem Jahr oder bis zu zehn Jahre in der Zukunft
    private fun isPlausible(date: LocalDate): Boolean {
        val diff = date.toEpochDays() - DateHelper.today().toEpochDays()
        return diff in -366L..3660L
    }
}

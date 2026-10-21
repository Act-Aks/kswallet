package com.kswallet.core.testing

import com.kswallet.core.common.time.DateRange
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.common.time.firstDayOfMonth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Deterministic clock for tests: "today" is a value, not a surprise.
 *
 * ```kotlin
 * val time = FixedTimeProvider.of("2026-10-12")
 * ```
 */
@OptIn(ExperimentalTime::class)
class FixedTimeProvider(
    private val fixedInstant: Instant,
    private val zone: TimeZone = TimeZone.UTC,
) : TimeProvider {

    override fun now(): Instant = fixedInstant

    override fun today(): LocalDate = fixedInstant.toLocalDateTime(zone).date

    override fun timeZone(): TimeZone = zone

    override fun currentMonth(): DateRange {
        val start = today().firstDayOfMonth()
        val next = start.plus(1, DateTimeUnit.MONTH)
        return DateRange(start.atStartOfDayIn(zone), next.atStartOfDayIn(zone))
    }

    companion object {
        /** Midday of [date] so that time-zone math in tests can never shift the day. */
        fun of(date: String, zone: TimeZone = TimeZone.UTC): FixedTimeProvider =
            FixedTimeProvider(LocalDate.parse(date).atStartOfDayIn(zone) + 12.hours, zone)
    }
}

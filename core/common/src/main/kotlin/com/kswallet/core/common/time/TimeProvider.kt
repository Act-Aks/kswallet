package com.kswallet.core.common.time

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The single source of "now" in the app.
 *
 * Nothing in domain/data calls `Clock.System` directly, which makes date logic
 * (period filters, "this month" summaries, seed timestamps) deterministic in tests:
 * inject a `FixedTimeProvider` and every screen shows the same day forever.
 */
interface TimeProvider {
    fun now(): Instant
    fun today(): LocalDate
    fun timeZone(): TimeZone

    /** Start of the month containing [now], inclusive. */
    fun currentMonth(): DateRange
}

@OptIn(ExperimentalTime::class)
class SystemTimeProvider(
    private val clock: Clock = Clock.System,
    private val zone: TimeZone = TimeZone.currentSystemDefault(),
) : TimeProvider {

    override fun now(): Instant = clock.now()

    override fun today(): LocalDate = now().toLocalDateTime(zone).date

    override fun timeZone(): TimeZone = zone

    override fun currentMonth(): DateRange {
        val date = today()
        val startOfMonth = date.firstDayOfMonth()
        val startOfNextMonth = startOfMonth.plus(1, DateTimeUnit.MONTH)
        return DateRange(
            start = startOfMonth.atStartOfDayIn(zone),
            endExclusive = startOfNextMonth.atStartOfDayIn(zone),
        )
    }
}

/** Utility so tests can construct ranges without repeating date juggling. */
fun LocalDate.firstDayOfMonth(): LocalDate = LocalDate(year, month, 1)

fun TimeProvider.monthRangeOf(date: LocalDate): DateRange {
    val startOfMonth = date.firstDayOfMonth()
    val startOfNextMonth = startOfMonth.plus(1, DateTimeUnit.MONTH)
    return DateRange(
        start = startOfMonth.atStartOfDayIn(timeZone()),
        endExclusive = startOfNextMonth.atStartOfDayIn(timeZone()),
    )
}

fun TimeProvider.lastNDaysRange(days: Int): DateRange {
    val today = today()
    return DateRange(
        start = today.minus(days - 1, DateTimeUnit.DAY).atStartOfDayIn(timeZone()),
        endExclusive = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone()),
    )
}

fun TimeProvider.yearRange(): DateRange = yearRangeOf(today())

/**
 * The calendar year containing [date].
 *
 * Budgets are the second consumer of these helpers: a yearly budget has to agree with the
 * "this year" filter of the ledger, which is exactly what sharing one function guarantees.
 */
fun TimeProvider.yearRangeOf(date: LocalDate): DateRange = DateRange(
    start = LocalDate(date.year, 1, 1).atStartOfDayIn(timeZone()),
    endExclusive = LocalDate(date.year + 1, 1, 1).atStartOfDayIn(timeZone()),
)

/**
 * The ISO week (Monday-based) containing [date].
 *
 * Weeks are the only period where "when does it start?" is a real question: Monday is the
 * ISO answer, and `DayOfWeek` in kotlinx-datetime is ordered from Monday, so the offset is
 * the day's ordinal.
 */
fun TimeProvider.weekRangeOf(date: LocalDate): DateRange {
    val startOfWeek = date.minus(date.dayOfWeek.ordinal, DateTimeUnit.DAY)
    return DateRange(
        start = startOfWeek.atStartOfDayIn(timeZone()),
        endExclusive = startOfWeek.plus(7, DateTimeUnit.DAY).atStartOfDayIn(timeZone()),
    )
}

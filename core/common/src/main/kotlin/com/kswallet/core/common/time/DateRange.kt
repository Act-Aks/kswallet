package com.kswallet.core.common.time

import kotlin.time.Instant

/**
 * A half-open instant range `[start, endExclusive)`.
 *
 * Used by every "period" filter in the app (this month, last 30 days, ...) and by the
 * DAO queries, so that SQL and Kotlin always agree on the boundaries.
 */
data class DateRange(val start: Instant, val endExclusive: Instant) {

    init {
        require(start <= endExclusive) { "start ($start) must not be after endExclusive ($endExclusive)" }
    }

    operator fun contains(instant: Instant): Boolean = instant in start..<endExclusive

    val durationInMillis: Long get() = endExclusive.toEpochMilliseconds() - start.toEpochMilliseconds()

    companion object {
        /** Covers every instant; handy for an "All time" filter. */
        val All: DateRange = DateRange(Instant.DISTANT_PAST, Instant.DISTANT_FUTURE)

        fun of(start: Instant, endExclusive: Instant) = DateRange(start, endExclusive)
    }
}

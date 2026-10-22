package com.kswallet.feature.reports.domain.model

import kotlinx.datetime.LocalDate

/**
 * One calendar month, as a report sees it.
 *
 * Deliberately not a `java.time` type: the domain layer is plain Kotlin, and the only two
 * things a report needs from a month - how long it is and which month comes before/after it -
 * are three lines of arithmetic. Nothing here has to agree with the ledger's *time zone*,
 * because a month is a calendar concept, not an instant range: the range is derived in the
 * data layer from `TimeProvider`, which owns the zone.
 */
data class ReportMonth(val year: Int, val month: Int) {

    init {
        require(month in 1..12) { "month must be 1..12, was $month" }
    }

    /** 28, 29, 30 or 31 - the month a bar chart has to draw. */
    val daysInMonth: Int
        get() = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            else -> if (isLeapYear(year)) 29 else 28
        }

    fun previous(): ReportMonth = if (month == 1) ReportMonth(year - 1, 12) else ReportMonth(year, month - 1)

    fun next(): ReportMonth = if (month == 12) ReportMonth(year + 1, 1) else ReportMonth(year, month + 1)

    fun contains(year: Int, month: Int): Boolean = this.year == year && this.month == month

    override fun toString(): String = "$year-${month.toString().padStart(2, '0')}"

    companion object {
        /**
         * `Month` is an enum ordered from January, so the ordinal is the month number. That
         * is deliberate: the newer `LocalDate.month.number` spelling does not exist in every
         * kotlinx-datetime version this project can build against, while the ordinal does.
         */
        fun of(date: LocalDate): ReportMonth = ReportMonth(date.year, date.month.ordinal + 1)

        private fun isLeapYear(year: Int): Boolean =
            (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    }
}

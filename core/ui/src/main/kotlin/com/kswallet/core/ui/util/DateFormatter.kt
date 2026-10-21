package com.kswallet.core.ui.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant
import java.time.Instant as JavaInstant

/**
 * Date/time rendering for lists and headers.
 *
 * Conversions between `kotlin.time.Instant` (used by domain and data) and `java.time`
 * (used for formatting) happen here and nowhere else.
 */
object DateFormatter {

    // Patterns are built once, but the locale is applied on every call: the user can change
    // the system language while the app is running, and a formatter captured at class-init
    // time would keep formatting in the language the process started with.
    private val dayMonth = DateTimeFormatter.ofPattern("d MMM")
    private val dayMonthYear = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val monthYear = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val timeOfDay = DateTimeFormatter.ofPattern("h:mm a")
    private val dayOfWeek = DateTimeFormatter.ofPattern("EEEE")

    private fun DateTimeFormatter.localized(): DateTimeFormatter = withLocale(Locale.getDefault())

    fun toJavaInstant(instant: Instant): JavaInstant = JavaInstant.ofEpochMilli(instant.toEpochMilliseconds())

    fun toDate(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): LocalDate =
        toJavaInstant(instant).atZone(zone).toLocalDate()

    fun toInstant(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Instant =
        Instant.fromEpochMilliseconds(date.atStartOfDay(zone).toInstant().toEpochMilli())

    /** `12 Oct` */
    fun shortDate(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        dayMonth.localized().format(toJavaInstant(instant).atZone(zone))

    /** `12 Oct 2026` */
    fun fullDate(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        dayMonthYear.localized().format(toJavaInstant(instant).atZone(zone))

    /** `October 2026` */
    fun monthAndYear(date: LocalDate): String = monthYear.localized().format(date)

    /** `4:35 PM` */
    fun time(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        timeOfDay.localized().format(toJavaInstant(instant).atZone(zone))

    /**
     * List header text: `Today`, `Yesterday`, `Monday` for the last week, then the date.
     */
    fun listHeader(instant: Instant, today: LocalDate, zone: ZoneId = ZoneId.systemDefault()): String {
        val date = toDate(instant, zone)
        return when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> if (date.isAfter(today.minusDays(7))) {
                dayOfWeek.localized().format(date)
            } else {
                dayMonthYear.localized().format(date)
            }
        }
    }
}

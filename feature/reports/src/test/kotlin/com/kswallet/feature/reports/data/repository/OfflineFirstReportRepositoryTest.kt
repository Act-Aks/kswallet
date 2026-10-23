package com.kswallet.feature.reports.data

import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.database.dao.TransactionDao
import com.kswallet.core.database.model.CashFlowPointRow
import com.kswallet.core.database.model.CashFlowRow
import com.kswallet.core.database.model.CategorySpendWithDetailsRow
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.testing.FixedTimeProvider
import com.kswallet.feature.reports.data.repository.OfflineFirstReportRepository
import com.kswallet.feature.reports.domain.model.ReportMonth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * What the repository is responsible for is not the SQL (Room tests that) but the four things
 * around it: which window each query is asked for, what "day" means in the user's time zone,
 * how many days have elapsed, and which gaps get filled with zeros. Those are exactly the
 * things a report can be quietly wrong about, so they are pinned here.
 */
class OfflineFirstReportRepositoryTest {

    private val currency = Currency.INR
    private val kolkata = TimeZone.of("Asia/Kolkata")
    private val timeProvider: TimeProvider = FixedTimeProvider.of("2026-10-12", kolkata)

    private val dao = mockk<TransactionDao>()

    private fun repository() = OfflineFirstReportRepository(dao, timeProvider)

    /** The window a Kotlin-side date computation produces, computed independently of the code. */
    private fun startOfMonthMillis(year: Int, month: Int): Long =
        LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.of("Asia/Kolkata"))
            .toInstant()
            .toEpochMilli()

    private fun stubDao(
        cashFlow: CashFlowRow = CashFlowRow(incomeMinor = 0, expenseMinor = 0),
        previousMonth: CashFlowRow = CashFlowRow(incomeMinor = 0, expenseMinor = 0),
        categories: List<CategorySpendWithDetailsRow> = emptyList(),
        points: List<CashFlowPointRow> = emptyList(),
    ) {
        every { dao.observeCashFlow(any(), any(), any()) } returnsMany
            listOf(flowOf(cashFlow), flowOf(previousMonth))
        every { dao.observeCategorySpendWithDetails(any(), any(), any()) } returns flowOf(categories)
        every { dao.observeCashFlowPoints(any(), any(), any()) } returns flowOf(points)
    }

    @Test
    fun `the report of a month is the half-open window of that month in the user's zone`() = runTest {
        stubDao()

        repository().observeMonthlyReport(ReportMonth(2026, 11), currency).first()

        val november = startOfMonthMillis(2026, 11)
        val december = startOfMonthMillis(2026, 12)
        verify { dao.observeCashFlow(november, december, "INR") }
        verify { dao.observeCategorySpendWithDetails(november, december, "INR") }
        verify { dao.observeCashFlowPoints(november, december, "INR") }
    }

    @Test
    fun `the comparison window is the month before, across a year boundary`() = runTest {
        stubDao()

        repository().observeMonthlyReport(ReportMonth(2026, 1), currency).first()

        val december = startOfMonthMillis(2025, 12)
        val january = startOfMonthMillis(2026, 1)
        verify { dao.observeCashFlow(december, january, "INR") }
    }

    @Test
    fun `only the accounts of the reported currency are summed`() = runTest {
        stubDao()

        repository().observeMonthlyReport(ReportMonth(2026, 10), Currency.USD).first()

        verify { dao.observeCashFlow(any(), any(), "USD") }
    }

    @Test
    fun `a running month averages over the days that have happened`() = runTest {
        stubDao(cashFlow = CashFlowRow(incomeMinor = 0, expenseMinor = 1_200_00))

        // FixedTimeProvider says today is 12 October 2026.
        val report = repository().observeMonthlyReport(ReportMonth(2026, 10), currency).first()

        assertEquals(12, report.daysElapsed)
        assertEquals(Money.of("100.00", currency), report.averagePerDay)
        assertTrue(report.isCurrentMonth)
    }

    @Test
    fun `a finished month averages over its whole length`() = runTest {
        stubDao(cashFlow = CashFlowRow(incomeMinor = 0, expenseMinor = 3_100_00))

        // September has 30 days, all of them spent.
        val report = repository().observeMonthlyReport(ReportMonth(2026, 9), currency).first()

        assertEquals(30, report.daysElapsed)
        assertEquals(Money.of("103.33", currency), report.averagePerDay)
        assertFalse(report.isCurrentMonth)
    }

    @Test
    fun `the daily series has one entry per day of the month, zeros included`() = runTest {
        stubDao(
            points = listOf(
                point(day = 3, expense = 10_00),
                point(day = 3, expense = 5_00),
                point(day = 20, expense = 40_00),
            ),
        )

        val report = repository().observeMonthlyReport(ReportMonth(2026, 10), currency).first()

        assertEquals(31, report.dailyExpenses.size)
        assertEquals(Money.of("15.00", currency), report.dailyExpenses[2])
        assertEquals(Money.of("40.00", currency), report.dailyExpenses[19])
        assertEquals(Money.zero(currency), report.dailyExpenses[0])
        assertTrue(report.dailyExpenses.drop(20).all { it.isZero })
    }

    @Test
    fun `instants are rolled into days in the user's zone, not in UTC`() = runTest {
        // 2026-10-05T20:00Z is already the 6th in Kolkata (+05:30): the report must count it
        // as the 6th, or a late-evening expense would land on the wrong day of the chart.
        val instant = java.time.Instant.parse("2026-10-05T20:00:00Z").toEpochMilli()
        stubDao(points = listOf(CashFlowPointRow(instant, incomeMinor = 0, expenseMinor = 99_00)))

        val report = repository().observeMonthlyReport(ReportMonth(2026, 10), currency).first()

        assertEquals(Money.of("99.00", currency), report.dailyExpenses[5])
        assertEquals(Money.zero(currency), report.dailyExpenses[4])
    }

    @Test
    fun `shares add up to one, uncategorised spending included`() = runTest {
        stubDao(
            cashFlow = CashFlowRow(incomeMinor = 0, expenseMinor = 100_00),
            categories = listOf(
                categoryRow(id = 1, name = "Groceries", spent = 60_00),
                categoryRow(id = 2, name = "Transport", spent = 30_00),
                categoryRow(id = 0, name = null, spent = 10_00),
            ),
        )

        val report = repository().observeMonthlyReport(ReportMonth(2026, 10), currency).first()

        assertEquals(1f, report.categories.sumOf { it.share.toDouble() }.toFloat(), 0.0001f)
        assertNull(report.categories.last().categoryId)
        assertTrue(report.categories.last().isUncategorised)
        assertEquals(Money.of("60.00", currency), report.topCategory?.amount)
    }

    @Test
    fun `a month with no spending reports no comparison instead of a division by zero`() = runTest {
        stubDao(
            cashFlow = CashFlowRow(incomeMinor = 0, expenseMinor = 50_00),
            previousMonth = CashFlowRow(incomeMinor = 0, expenseMinor = 0),
        )

        val report = repository().observeMonthlyReport(ReportMonth(2026, 10), currency).first()

        assertNull(report.changeVsPreviousMonth)
        assertTrue(report.hasActivity)
    }

    @Test
    fun `the change against last month is a fraction of it`() = runTest {
        stubDao(
            cashFlow = CashFlowRow(incomeMinor = 0, expenseMinor = 150_00),
            previousMonth = CashFlowRow(incomeMinor = 0, expenseMinor = 100_00),
        )

        val report = repository().observeMonthlyReport(ReportMonth(2026, 10), currency).first()

        assertEquals(0.5f, report.changeVsPreviousMonth!!, 0.0001f)
    }

    private fun point(day: Int, expense: Long): CashFlowPointRow = CashFlowPointRow(
        occurredAt = LocalDate.of(2026, 10, day)
            .atTime(12, 0)
            .atZone(ZoneId.of("Asia/Kolkata"))
            .toInstant()
            .toEpochMilli(),
        incomeMinor = 0,
        expenseMinor = expense,
    )

    private fun categoryRow(id: Long, name: String?, spent: Long) = CategorySpendWithDetailsRow(
        categoryId = id,
        categoryName = name,
        categoryIconKey = "groceries",
        categoryColorArgb = 0xFF112233,
        transactionCount = 2,
        spentMinor = spent,
    )
}

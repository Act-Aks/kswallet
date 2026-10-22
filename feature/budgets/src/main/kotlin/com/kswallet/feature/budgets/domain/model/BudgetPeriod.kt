package com.kswallet.feature.budgets.domain.model

import com.kswallet.core.common.time.DateRange
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.common.time.monthRangeOf
import com.kswallet.core.common.time.weekRangeOf
import com.kswallet.core.common.time.yearRangeOf

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY,
    ;

    /**
     * The window this period currently covers, as of [timeProvider]'s clock.
     *
     * It reuses the same range helpers as the ledger filters ("this month", "this year"), so a
     * monthly budget and the Transactions screen can never disagree about where a month ends.
     */
    fun currentRange(timeProvider: TimeProvider): DateRange = when (this) {
        WEEKLY -> timeProvider.weekRangeOf(timeProvider.today())
        MONTHLY -> timeProvider.monthRangeOf(timeProvider.today())
        YEARLY -> timeProvider.yearRangeOf(timeProvider.today())
    }
}

package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.core.common.time.DateRange
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.common.time.lastNDaysRange
import com.kswallet.core.common.time.yearRange
import com.kswallet.feature.transactions.domain.model.PeriodOption

/** Turns a [com.kswallet.feature.transactions.domain.model.PeriodOption] into concrete instants using the injected clock. */
class ResolvePeriodUseCase(
    private val timeProvider: TimeProvider,
) {
    operator fun invoke(period: PeriodOption): DateRange = when (period) {
        PeriodOption.THIS_MONTH -> timeProvider.currentMonth()
        PeriodOption.LAST_30_DAYS -> timeProvider.lastNDaysRange(30)
        PeriodOption.THIS_YEAR -> timeProvider.yearRange()
        PeriodOption.ALL_TIME -> DateRange.All
    }
}

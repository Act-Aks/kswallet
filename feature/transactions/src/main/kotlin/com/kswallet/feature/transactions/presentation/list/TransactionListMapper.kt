package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.format.DateFormatter
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaZoneId

/**
 * Turns ledger rows into list sections.
 *
 * Pure and framework-free on purpose: the grouping rules (which day an entry belongs to, what
 * a day's subtotal means) are the kind of thing that silently goes wrong, so they are tested
 * directly instead of through a Compose test.
 */
object TransactionListMapper {

    fun toDayGroups(
        details: List<TransactionDetails>,
        today: LocalDate,
        timeZone: TimeZone,
        currency: Currency,
    ): List<DayGroupUi> {
        val zone = timeZone.toJavaZoneId()
        val javaToday = today.toJavaLocalDate()
        return details
            .groupBy { DateFormatter.toDate(it.dateTime, zone) }
            .map { (_, items) ->
                val ordered = items.sortedByDescending { it.transaction.dateTime }
                DayGroupUi(
                    header = DateFormatter.listHeader(ordered.first().dateTime, javaToday, zone),
                    dayTotal = dayTotal(ordered, currency),
                    items = ordered.map(::toItem),
                )
            }
    }

    /**
     * Transfers are movements between the user's own accounts, so they cancel out of a
     * day's subtotal instead of counting as spending.
     */
    fun dayTotal(details: List<TransactionDetails>, currency: Currency): Money =
        details.fold(Money.zero(currency)) { total, detail ->
            total + when (detail.type) {
                TransactionType.INCOME -> detail.amount
                TransactionType.EXPENSE -> -detail.amount
                TransactionType.TRANSFER -> Money.zero(currency)
            }
        }

    fun toItem(detail: TransactionDetails): TransactionItemUi = TransactionItemUi(
        id = detail.id,
        title = detail.categoryName ?: detail.transferAccountName ?: detail.accountName,
        subtitle = detail.subtitle(),
        amount = detail.amount,
        type = detail.type,
        iconKey = detail.categoryIconKey,
        colorArgb = detail.categoryColorArgb,
    )

    private fun TransactionDetails.subtitle(): String = when {
        note != null -> note.orEmpty()
        transferAccountName != null -> "$accountName \u2192 $transferAccountName"
        else -> accountName
    }
}

package com.kswallet.core.money

sealed interface MoneyParseResult {

    data class Success(
        val money: Money,
    ) : MoneyParseResult

    data class Failure(
        val reason: MoneyParseError,
    ) : MoneyParseResult
}
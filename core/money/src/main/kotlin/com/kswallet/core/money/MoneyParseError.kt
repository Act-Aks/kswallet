package com.kswallet.core.money

enum class MoneyParseError {
    Empty,
    InvalidFormat,
    InputTooLong,
    PrecisionLoss,
    OutOfRange,
}
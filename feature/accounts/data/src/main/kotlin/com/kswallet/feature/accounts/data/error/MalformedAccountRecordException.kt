package com.kswallet.feature.accounts.data.error

class MalformedAccountRecordException(
    val storedAccountId: String,
    cause: IllegalArgumentException,
) : IllegalStateException(
    "Stored account '$storedAccountId' violates account invariants.",
    cause,
)
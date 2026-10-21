package com.kswallet.core.model

/**
 * Typed identifiers. They cost nothing at runtime (inline value classes) and make it
 * impossible to pass a `CategoryId` where an `AccountId` is expected.
 *
 * `UNSAVED` marks an entity that has not been persisted yet; repositories assign the
 * real id when they insert it.
 */
@JvmInline
value class AccountId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        val UNSAVED = AccountId(0L)
    }
}

@JvmInline
value class CategoryId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        val UNSAVED = CategoryId(0L)
    }
}

@JvmInline
value class BudgetId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        val UNSAVED = BudgetId(0L)
    }
}

@JvmInline
value class TransactionId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        val UNSAVED = TransactionId(0L)
    }
}

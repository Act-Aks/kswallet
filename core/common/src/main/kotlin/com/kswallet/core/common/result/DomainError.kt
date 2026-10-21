package com.kswallet.core.common.result

import kotlin.coroutines.cancellation.CancellationException

/**
 * Every failure a use case or repository can hand to the presentation layer.
 *
 * Keeping the set small is deliberate: presentation maps it once (per feature) to
 * user-facing text and treats everything else generically.
 */
sealed interface DomainError {

    /** The requested entity does not exist (anymore). */
    data object NotFound : DomainError

    /** The write was rejected because it would break an invariant (e.g. delete an account with transactions). */
    data class Conflict(val reason: String) : DomainError

    /** Storage / database failure. */
    data class Data(val cause: Throwable? = null) : DomainError

    /** Anything unexpected. */
    data class Unknown(val cause: Throwable? = null) : DomainError

    companion object {
        fun from(throwable: Throwable): DomainError = when (throwable) {
            is IllegalStateException, is IllegalArgumentException -> Unknown(throwable)
            else -> Data(throwable)
        }
    }
}

/**
 * Maps a failure to a user-facing text bag that the presentation layer can resolve.
 * Feature modules override this for their own `DomainError` consumers with a local
 * string resource; this default keeps the boilerplate out of the way.
 */
fun DomainError.debugMessage(): String = when (this) {
    DomainError.NotFound -> "Not found"
    is DomainError.Conflict -> reason
    is DomainError.Data -> cause?.message ?: "Storage error"
    is DomainError.Unknown -> cause?.message ?: "Unexpected error"
}

/** Never swallow coroutine cancellation. */
fun Throwable.rethrowIfCancellation() {
    if (this is CancellationException) throw this
}

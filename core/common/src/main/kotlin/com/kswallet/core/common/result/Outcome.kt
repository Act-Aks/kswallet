package com.kswallet.core.common.result

/**
 * The result of a domain operation.
 *
 * Domain and data layers never leak exceptions to the presentation layer: a failure
 * travels as a [DomainError] that the presentation layer maps to a [com.kswallet.core.mvi.UiMessage].
 */
sealed interface Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>

    data class Failure(val error: DomainError) : Outcome<Nothing>
}

val Outcome<*>.isSuccess: Boolean get() = this is Outcome.Success
val Outcome<*>.isFailure: Boolean get() = this is Outcome.Failure

fun <T> Outcome<T>.valueOrNull(): T? = (this as? Outcome.Success)?.value
fun Outcome<*>.errorOrNull(): DomainError? = (this as? Outcome.Failure)?.error

inline fun <T, R> Outcome<T>.map(transform: (T) -> R): Outcome<R> = when (this) {
    is Outcome.Success -> Outcome.Success(transform(value))
    is Outcome.Failure -> this
}

inline fun <T> Outcome<T>.onSuccess(action: (T) -> Unit): Outcome<T> {
    if (this is Outcome.Success) action(value)
    return this
}

inline fun <T> Outcome<T>.onFailure(action: (DomainError) -> Unit): Outcome<T> {
    if (this is Outcome.Failure) action(error)
    return this
}

inline fun <T, R> Outcome<T>.fold(onSuccess: (T) -> R, onFailure: (DomainError) -> R): R = when (this) {
    is Outcome.Success -> onSuccess(value)
    is Outcome.Failure -> onFailure(error)
}

/**
 * Falls back to [fallback] when this is a failure.
 *
 * Recovery keeps the value type on purpose: a caller that needs a different value maps it
 * with [map], and one that needs a different *error* uses [mapError]. Keeping those two
 * conversions apart is what stops "which branch am I on?" bugs.
 */
inline fun <T> Outcome<T>.orElse(fallback: (DomainError) -> Outcome<T>): Outcome<T> = when (this) {
    is Outcome.Success -> this
    is Outcome.Failure -> fallback(error)
}

/** Replaces the error of a failure; a successful value is untouched. */
inline fun <T> Outcome<T>.mapError(transform: (DomainError) -> DomainError): Outcome<T> = when (this) {
    is Outcome.Success -> this
    is Outcome.Failure -> Outcome.Failure(transform(error))
}

/** Wraps a throwing block into an [Outcome], so callers inside a use case stay expression-friendly. */
inline fun <T> outcomeOf(block: () -> T): Outcome<T> = try {
    Outcome.Success(block())
} catch (throwable: Throwable) {
    Outcome.Failure(DomainError.from(throwable))
}

/** [outcomeOf] for suspending blocks. */
suspend inline fun <T> outcomeOfSuspend(crossinline block: suspend () -> T): Outcome<T> = try {
    Outcome.Success(block())
} catch (throwable: Throwable) {
    Outcome.Failure(DomainError.from(throwable))
}

/** Runs [block] and wraps it, allowing the block to signal a failure through [Outcome]. */
inline fun <T> runOutcome(block: () -> Outcome<T>): Outcome<T> = try {
    block()
} catch (throwable: Throwable) {
    Outcome.Failure(DomainError.from(throwable))
}

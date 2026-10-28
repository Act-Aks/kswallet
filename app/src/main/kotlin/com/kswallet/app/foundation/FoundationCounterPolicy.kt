package com.kswallet.app.foundation

class FoundationCounterPolicy {

    val maximum: Int = 10

    fun increment(current: Int): Int =
        (current + 1).coerceAtMost(maximum)
}
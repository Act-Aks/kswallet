package com.kswallet.app.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * The bottom bar is defined once, as an enum, and everything else reads it. These tests pin
 * the parts that are easy to break by accident: the order of the tabs, and the fact that no
 * two tabs share a route or an icon (which would silently make one of them unreachable or
 * visually identical). Six tabs is the limit, not a target: past that, the bar needs a "more"
 * destination instead of another icon.
 */
class KsWalletTopLevelDestinationTest {

    @Test
    fun `the bar shows the six tabs in the order the app is meant to be read`() {
        assertEquals(
            listOf("HOME", "TRANSACTIONS", "REPORTS", "BUDGETS", "ACCOUNTS", "CATEGORIES"),
            KsWalletTopLevelDestination.entries.map { it.name },
        )
    }

    @Test
    fun `every tab owns a unique route`() {
        // Navigation 3 routes are serializable keys; duplicate key types would make two tabs
        // indistinguishable to the entry provider.
        val routes = KsWalletTopLevelDestination.entries.map { it.route::class.qualifiedName }

        assertEquals(routes.size, routes.distinct().size)
        assertEquals(KsWalletTopLevelDestination.entries.size, routes.filterNotNull().size)
    }

    @Test
    fun `every tab owns a unique label and icon`() {
        val labels = KsWalletTopLevelDestination.entries.map { it.labelRes }
        val icons = KsWalletTopLevelDestination.entries.map { it.icon }

        assertEquals(labels.size, labels.distinct().size)
        assertEquals(icons.size, icons.distinct().size)
    }
}

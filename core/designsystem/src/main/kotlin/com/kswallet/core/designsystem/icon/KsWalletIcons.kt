package com.kswallet.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The icon registry.
 *
 * Icons are referenced by a stable string key so that a category can be persisted in
 * SQLite without any dependency on Compose. `KsWalletIcons.byKey` maps the key back to a vector
 * and falls back to a neutral icon for unknown keys (forward compatible).
 */
data class KsWalletIcon(val key: String, val icon: ImageVector)

object KsWalletIcons {

    val all: List<KsWalletIcon> = listOf(
        KsWalletIcon("home", Icons.Filled.Home),
        KsWalletIcon("groceries", Icons.Filled.LocalGroceryStore),
        KsWalletIcon("restaurant", Icons.Filled.Restaurant),
        KsWalletIcon("fastfood", Icons.Filled.Fastfood),
        KsWalletIcon("shopping", Icons.Filled.ShoppingBag),
        KsWalletIcon("transport", Icons.Filled.DirectionsCar),
        KsWalletIcon("travel", Icons.Filled.Flight),
        KsWalletIcon("health", Icons.Filled.MedicalServices),
        KsWalletIcon("fitness", Icons.Filled.FitnessCenter),
        KsWalletIcon("education", Icons.Filled.School),
        KsWalletIcon("entertainment", Icons.Filled.SportsEsports),
        KsWalletIcon("movies", Icons.Filled.Movie),
        KsWalletIcon("pets", Icons.Filled.Pets),
        KsWalletIcon("bills", Icons.Filled.Receipt),
        KsWalletIcon("income", Icons.Filled.AttachMoney),
        KsWalletIcon("salary", Icons.Filled.Work),
        KsWalletIcon("gift", Icons.Filled.CardGiftcard),
        KsWalletIcon("savings", Icons.Filled.Savings),
    )

    const val fallbackKey: String = "category"

    private val byKey: Map<String, ImageVector> = all.associate { it.key to it.icon }

    fun byKey(key: String): ImageVector = byKey[key] ?: Icons.Filled.Category

    // --- account type icons ---

    val cash: ImageVector = Icons.Filled.Payments
    val bank: ImageVector = Icons.Filled.AccountBalance
    val card: ImageVector = Icons.Filled.CreditCard
    val wallet: ImageVector = Icons.Filled.AccountBalanceWallet
    val crypto: ImageVector = Icons.Filled.CurrencyBitcoin
    val other: ImageVector = Icons.Filled.Category

    // --- app-level icons ---

    val budgets: ImageVector = Icons.Filled.Savings
    val reports: ImageVector = Icons.Filled.PieChart
    val settings: ImageVector = Icons.Filled.Settings

    // --- directional icons: the extended set, exposed here so features never depend on it ---

    val chevronLeft: ImageVector = Icons.Filled.ChevronLeft
    val chevronRight: ImageVector = Icons.Filled.ChevronRight
}

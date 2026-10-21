package com.kswallet.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.kswallet.core.designsystem.theme.KsWalletColors
import com.kswallet.core.designsystem.theme.MoneyTextStyle
import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.util.MoneyFormatter

/**
 * Renders a [Money] with the wallet's color language:
 * green for income, red for expense, blue for transfers - plus a `+`/`-` sign, so the
 * meaning survives for anyone who cannot tell those colors apart.
 */
@Composable
fun KsWalletAmountText(
    money: Money,
    modifier: Modifier = Modifier,
    type: TransactionType? = null,
    style: TextStyle = MoneyTextStyle,
    showSign: Boolean = type != null,
) {
    val color = when {
        type == null -> MaterialTheme.colorScheme.onSurface
        money.isZero -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> amountColor(type)
    }
    Text(
        text = if (showSign) MoneyFormatter.formatSigned(money) else MoneyFormatter.format(money),
        modifier = modifier,
        style = style,
        color = color,
    )
}

@Composable
fun amountColor(type: TransactionType): Color = when (type) {
    TransactionType.INCOME -> KsWalletColors.Income
    TransactionType.EXPENSE -> KsWalletColors.Expense
    TransactionType.TRANSFER -> KsWalletColors.Transfer
}

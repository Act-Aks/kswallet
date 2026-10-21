package com.kswallet.feature.accounts.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.model.AccountType
import com.kswallet.feature.accounts.R

@Composable
internal fun AccountType.icon() = when (this) {
    AccountType.CASH -> KsWalletIcons.cash
    AccountType.BANK -> KsWalletIcons.bank
    AccountType.CARD -> KsWalletIcons.card
    AccountType.WALLET -> KsWalletIcons.wallet
    AccountType.CRYPTO -> KsWalletIcons.crypto
    AccountType.OTHER -> KsWalletIcons.other
}

@Composable
internal fun AccountType.label(): String = when (this) {
    AccountType.CASH -> stringResource(R.string.account_type_cash)
    AccountType.BANK -> stringResource(R.string.account_type_bank)
    AccountType.CARD -> stringResource(R.string.account_type_card)
    AccountType.WALLET -> stringResource(R.string.account_type_wallet)
    AccountType.CRYPTO -> stringResource(R.string.account_type_crypto)
    AccountType.OTHER -> stringResource(R.string.account_type_other)
}

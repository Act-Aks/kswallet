package com.kswallet.feature.accounts.presentation

sealed interface AccountsContent {

    data object Loading : AccountsContent

    data object Empty : AccountsContent

    data class Content(
        val accounts: List<AccountListItem>,
    ) : AccountsContent {
        init {
            require(accounts.isNotEmpty()) {
                "Account content requires at least one account."
            }
        }
    }

    data object Error : AccountsContent
}
package com.kswallet.feature.accounts.presentation.editor

import androidx.lifecycle.viewModelScope
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.accounts.R
import com.kswallet.feature.accounts.domain.usecase.GetAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.SaveAccountResult
import com.kswallet.feature.accounts.domain.usecase.SaveAccountUseCase
import com.kswallet.feature.accounts.domain.validation.AccountFormError
import com.kswallet.feature.accounts.domain.validation.AccountFormInput
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

/**
 * Create/edit a wallet.
 *
 * The screen is intentionally "dumb": it sends what the user typed and renders whatever the
 * state says, including which field is wrong and why.
 */
@KoinViewModel
class AccountEditorViewModel(
    @InjectedParam accountIdValue: Long,
    private val getAccount: GetAccountUseCase,
    private val saveAccount: SaveAccountUseCase,
    private val settingsRepository: SettingsRepository,
) : ScreenViewModel<AccountEditorState, AccountEditorAction, AccountEditorEvent>(
    AccountEditorState(isNew = accountIdValue == 0L, isLoading = accountIdValue != 0L),
) {

    private val accountId = AccountId(accountIdValue)
    private var existing: Account? = null

    init {
        viewModelScope.launch {
            val defaultCurrency = settingsRepository.observeSettings().first().defaultCurrency
            if (accountId.isSaved) {
                when (val loaded = getAccount(accountId)) {
                    is com.kswallet.core.common.result.Outcome.Success -> {
                        existing = loaded.value
                        setState {
                            copy(
                                isLoading = false,
                                isNew = false,
                                name = loaded.value.name,
                                type = loaded.value.type,
                                currency = loaded.value.currency,
                                initialBalanceText = loaded.value.initialBalance.toMajorUnits().toPlainString(),
                                colorArgb = loaded.value.colorArgb,
                                includedInTotal = loaded.value.includedInTotal,
                            )
                        }
                    }

                    is com.kswallet.core.common.result.Outcome.Failure -> {
                        setState { copy(isLoading = false) }
                        sendEvent(AccountEditorEvent.ShowMessage(UiMessage.Resource(R.string.error_not_found)))
                    }
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        currency = defaultCurrency,
                        colorArgb = DEFAULT_COLOR,
                    )
                }
            }
        }
    }

    override fun onAction(action: AccountEditorAction) {
        when (action) {
            is AccountEditorAction.NameChanged ->
                setState { copy(name = action.name, nameError = null) }

            is AccountEditorAction.TypeChanged ->
                setState { copy(type = action.type) }

            is AccountEditorAction.CurrencyChanged ->
                setState { copy(currency = action.currency, balanceError = null) }

            is AccountEditorAction.InitialBalanceChanged ->
                setState {
                    copy(
                        initialBalanceText = MoneyFormatter.sanitizeInput(
                            input = action.text,
                            decimalDigits = currency.decimalDigits,
                        ),
                        balanceError = null,
                    )
                }

            is AccountEditorAction.ColorChanged -> setState { copy(colorArgb = action.colorArgb) }

            is AccountEditorAction.IncludedInTotalChanged ->
                setState { copy(includedInTotal = action.included) }

            AccountEditorAction.BackClicked -> sendEvent(AccountEditorEvent.NavigateBack())

            AccountEditorAction.SaveClicked -> save()
        }
    }

    private fun save() {
        if (currentState.isSaving) return
        setState { copy(isSaving = true, nameError = null, balanceError = null) }
        viewModelScope.launch {
            val input = AccountFormInput(
                name = currentState.name,
                type = currentState.type,
                currency = currentState.currency,
                initialBalanceText = currentState.initialBalanceText,
                colorArgb = currentState.colorArgb,
                includedInTotal = currentState.includedInTotal,
            )
            when (val result = saveAccount(input, existing)) {
                is SaveAccountResult.Saved -> {
                    setState { copy(isSaving = false) }
                    sendEvent(AccountEditorEvent.NavigateBack(saved = true))
                }

                is SaveAccountResult.Invalid -> {
                    setState {
                        copy(
                            isSaving = false,
                            nameError = result.errors.mapToEditorError(
                                AccountFormError.EmptyName,
                                AccountFormError.NameTooLong,
                            ),
                            balanceError = result.errors.firstOrNullOf(AccountFormError.InvalidInitialBalance),
                        )
                    }
                }

                is SaveAccountResult.Failed -> {
                    setState { copy(isSaving = false) }
                    sendEvent(AccountEditorEvent.ShowMessage(UiMessage.Resource(R.string.error_storage)))
                }
            }
        }
    }

    private fun List<AccountFormError>.mapToEditorError(
        empty: AccountFormError,
        tooLong: AccountFormError,
    ): AccountEditorError? = when {
        contains(empty) -> AccountEditorError.EmptyName
        contains(tooLong) -> AccountEditorError.NameTooLong
        else -> null
    }

    private fun List<AccountFormError>.firstOrNullOf(error: AccountFormError): AccountEditorError? =
        if (contains(error)) AccountEditorError.InvalidInitialBalance else null

    private companion object {
        const val DEFAULT_COLOR: Long = 0xFF0E7C66
    }
}

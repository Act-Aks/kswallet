package com.kswallet.feature.transactions.presentation.editor

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.model.Account
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.DateFormatter
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountsUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import com.kswallet.feature.transactions.R
import com.kswallet.feature.transactions.domain.usecase.GetTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.SaveTransactionResult
import com.kswallet.feature.transactions.domain.usecase.SaveTransactionUseCase
import com.kswallet.feature.transactions.domain.validation.TransactionFormError
import com.kswallet.feature.transactions.domain.validation.TransactionFormInput
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaZoneId
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

/**
 * Create/edit one ledger entry.
 *
 * The account and category catalogs come from the accounts/categories features through
 * their *domain* layer, so this module never sees their screens or ViewModels. The forms
 * are prefilled exactly once - a running catalog update must not overwrite what the user
 * is typing.
 */
@KoinViewModel
class TransactionEditorViewModel(
    @InjectedParam transactionIdValue: Long,
    private val observeAccounts: ObserveAccountsUseCase,
    private val observeCategories: ObserveCategoriesUseCase,
    private val getTransaction: GetTransactionUseCase,
    private val saveTransaction: SaveTransactionUseCase,
    private val timeProvider: TimeProvider,
) : ScreenViewModel<TransactionEditorState, TransactionEditorAction, TransactionEditorEvent>(
    TransactionEditorState(isNew = transactionIdValue == 0L, isLoading = true),
) {

    private val transactionId = TransactionId(transactionIdValue)
    private var existing: Transaction? = null
    private var catalogAccounts: List<Account> = emptyList()
    private var catalogCategories: List<Category> = emptyList()
    private var prefilled = false

    init {
        viewModelScope.launch {
            if (transactionId.isSaved) {
                when (val loaded = getTransaction(transactionId)) {
                    is Outcome.Success -> existing = loaded.value
                    is Outcome.Failure -> sendEvent(
                        TransactionEditorEvent.ShowMessage(UiMessage.Resource(R.string.error_not_found)),
                    )
                }
            }

            combine(observeAccounts(), observeCategories()) { accounts, categories -> accounts to categories }
                .collect { (accounts, categories) -> onCatalog(accounts, categories.map { it.category }) }
        }
    }

    override fun onAction(action: TransactionEditorAction) {
        when (action) {
            is TransactionEditorAction.TypeChanged -> setState {
                copy(
                    type = action.type,
                    // A category of the other type would not validate, so default to a valid one.
                    categoryId = categories.firstOrNull { it.type == CategoryType.forTransactionType(action.type) }?.id,
                    transferAccountId = transferAccountId.takeIf { action.type == TransactionType.TRANSFER },
                    errors = emptySet(),
                )
            }

            is TransactionEditorAction.AmountChanged -> setState {
                copy(
                    amountText = MoneyFormatter.sanitizeInput(action.text, currency.decimalDigits),
                    errors = emptySet(),
                )
            }

            is TransactionEditorAction.AccountSelected -> setState {
                copy(accountId = action.id, transferAccountId = transferAccountId.takeIf { it != action.id })
            }

            is TransactionEditorAction.TransferAccountSelected -> setState {
                copy(transferAccountId = action.id, errors = emptySet())
            }

            is TransactionEditorAction.CategorySelected -> setState {
                copy(categoryId = action.id, errors = emptySet())
            }

            is TransactionEditorAction.NoteChanged -> setState { copy(note = action.note) }

            TransactionEditorAction.BackClicked -> sendEvent(TransactionEditorEvent.NavigateBack())

            TransactionEditorAction.SaveClicked -> save()
        }
    }

    private fun onCatalog(accounts: List<Account>, categories: List<Category>) {
        catalogAccounts = accounts
        catalogCategories = categories

        val accountOptions = accounts.map { AccountOptionUi(it.id, it.name, it.currency) }
        val categoryOptions = categories.map { CategoryOptionUi(it.id, it.name, it.type, it.iconKey) }

        if (prefilled) {
            setState { copy(isLoading = false, accounts = accountOptions, categories = categoryOptions) }
            return
        }

        prefilled = true
        val entryType = existing?.type ?: currentState.type
        val defaultAccountId = existing?.accountId ?: accounts.firstOrNull()?.id
        val defaultCategoryId = existing?.categoryId
            ?: categories.firstOrNull { it.type == CategoryType.forTransactionType(entryType) }?.id

        setState {
            copy(
                isLoading = false,
                type = entryType,
                amountText = existing?.amount?.toMajorUnits()?.toPlainString().orEmpty(),
                note = existing?.note.orEmpty(),
                accountId = defaultAccountId,
                transferAccountId = existing?.transferAccountId,
                categoryId = defaultCategoryId,
                accounts = accountOptions,
                categories = categoryOptions,
                dateLabel = DateFormatter.fullDate(
                    instant = existing?.dateTime ?: timeProvider.now(),
                    zone = timeProvider.timeZone().toJavaZoneId(),
                ),
            )
        }
    }

    private fun save() {
        if (currentState.isSaving) return
        setState { copy(isSaving = true, errors = emptySet()) }

        viewModelScope.launch {
            val input = TransactionFormInput(
                type = currentState.type,
                amountText = currentState.amountText,
                accountId = currentState.accountId,
                transferAccountId = currentState.transferAccountId,
                categoryId = currentState.categoryId,
                note = currentState.note,
            )

            when (val result = saveTransaction(input, catalogAccounts, catalogCategories, existing)) {
                is SaveTransactionResult.Saved -> {
                    setState { copy(isSaving = false) }
                    sendEvent(TransactionEditorEvent.NavigateBack(saved = true))
                }

                is SaveTransactionResult.Invalid -> setState {
                    copy(isSaving = false, errors = result.errors.map { it.toEditorError() }.toSet())
                }

                is SaveTransactionResult.Failed -> {
                    setState { copy(isSaving = false) }
                    sendEvent(TransactionEditorEvent.ShowMessage(UiMessage.Resource(R.string.error_storage)))
                }
            }
        }
    }
}

private fun TransactionFormError.toEditorError(): TransactionEditorError = when (this) {
    TransactionFormError.InvalidAmount -> TransactionEditorError.InvalidAmount
    TransactionFormError.AmountNotPositive -> TransactionEditorError.AmountNotPositive
    TransactionFormError.AccountMissing -> TransactionEditorError.AccountMissing
    TransactionFormError.TransferTargetMissing -> TransactionEditorError.TransferTargetMissing
    TransactionFormError.TransferTargetSameAsSource -> TransactionEditorError.TransferTargetSameAsSource
    TransactionFormError.CategoryMissing -> TransactionEditorError.CategoryMissing
    TransactionFormError.CurrencyMismatch -> TransactionEditorError.CurrencyMismatch
    TransactionFormError.TransferNotAllowed -> TransactionEditorError.Unknown
}

package com.kswallet.feature.budgets.presentation.editor

import androidx.lifecycle.viewModelScope
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.budgets.R
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.SaveBudgetResult
import com.kswallet.feature.budgets.domain.usecase.SaveBudgetUseCase
import com.kswallet.feature.budgets.domain.validation.BudgetFormError
import com.kswallet.feature.budgets.domain.validation.BudgetFormInput
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

/**
 * The budget editor: pick a category, type a limit, choose a period.
 *
 * The id arrives as a plain `Long` (0 = new) because Koin passes it as a call-site parameter.
 * The currency is never asked for: a budget is counted in the user's base currency, which the
 * save use case parses the typed amount with.
 */
@KoinViewModel
class BudgetEditorViewModel(
    @InjectedParam budgetIdValue: Long,
    observeBudget: ObserveBudgetUseCase,
    observeCategories: ObserveCategoriesUseCase,
    observeBaseCurrency: ObserveBaseCurrencyUseCase,
    private val saveBudget: SaveBudgetUseCase,
) : ScreenViewModel<BudgetEditorState, BudgetEditorAction, BudgetEditorEvent>(
    BudgetEditorState(isNew = budgetIdValue == 0L),
) {

    private val budgetId = BudgetId(budgetIdValue)
    private var existing: Budget? = null

    init {
        observeBaseCurrency()
            .onEach { currency -> setState { copy(currency = currency) } }
            .launchIn(viewModelScope)

        observeCategories(CategoryType.EXPENSE)
            .onEach { summaries ->
                setState {
                    copy(
                        // The form is usable as soon as the picker has content; for a new
                        // budget that is all it needs.
                        isLoading = if (isNew) false else isLoading,
                        categories = summaries.map { summary ->
                            BudgetCategoryOptionUi(
                                id = summary.category.id,
                                name = summary.category.name,
                                iconKey = summary.category.iconKey,
                            )
                        },
                    )
                }
            }
            .launchIn(viewModelScope)

        if (budgetId.isSaved) {
            observeBudget(budgetId)
                .onEach { loaded ->
                    if (loaded != null) {
                        existing = loaded
                        setState {
                            copy(
                                isLoading = false,
                                isNew = false,
                                categoryId = loaded.categoryId,
                                limitText = loaded.limit.toMajorUnits().toPlainString(),
                                period = loaded.period,
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    override fun onAction(action: BudgetEditorAction) {
        when (action) {
            is BudgetEditorAction.CategorySelected -> setState {
                copy(categoryId = action.id, error = null)
            }

            is BudgetEditorAction.LimitChanged -> setState {
                copy(
                    limitText = MoneyFormatter.sanitizeInput(action.text, currency.decimalDigits),
                    error = null,
                )
            }

            is BudgetEditorAction.PeriodChanged -> setState { copy(period = action.period, error = null) }
            BudgetEditorAction.BackClicked -> sendEvent(BudgetEditorEvent.NavigateBack())
            BudgetEditorAction.SaveClicked -> save()
        }
    }

    private fun save() {
        if (currentState.isSaving) return
        setState { copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val input = BudgetFormInput(
                categoryId = currentState.categoryId,
                limitText = currentState.limitText,
                period = currentState.period,
            )
            val result = saveBudget(existing, input, currentState.currency)
            setState { copy(isSaving = false) }

            when (result) {
                is SaveBudgetResult.Saved -> sendEvent(BudgetEditorEvent.NavigateBack(saved = true))

                is SaveBudgetResult.Invalid ->
                    setState { copy(error = result.errors.first().toEditorError()) }

                is SaveBudgetResult.Failed -> sendEvent(
                    BudgetEditorEvent.ShowMessage(UiMessage.Resource(R.string.error_storage)),
                )
            }
        }
    }
}

private fun BudgetFormError.toEditorError(): BudgetEditorError = when (this) {
    BudgetFormError.CategoryMissing -> BudgetEditorError.CategoryMissing
    BudgetFormError.InvalidLimit -> BudgetEditorError.InvalidLimit
    BudgetFormError.LimitNotPositive -> BudgetEditorError.LimitNotPositive
}

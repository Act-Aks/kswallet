package com.kswallet.feature.categories.presentation.list

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.categories.domain.usecase.DeleteCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import com.kswallet.feature.categories.presentation.error.toUiMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModel(
    private val observeCategories: ObserveCategoriesUseCase,
    private val deleteCategory: DeleteCategoryUseCase,
) : ScreenViewModel<CategoriesState, CategoriesAction, CategoriesEvent>(CategoriesState()) {

    private val selectedType = MutableStateFlow(CategoryType.EXPENSE)

    init {
        // One stream, restarted whenever the tab changes: `flatMapLatest` cancels the
        // previous query so a slow database can never deliver stale rows.
        selectedType
            .flatMapLatest { type -> observeCategories(type) }
            .onEach { summaries ->
                setState {
                    copy(
                        isLoading = false,
                        categories = summaries.map { summary ->
                            CategoryRowUi(
                                id = summary.category.id,
                                name = summary.category.name,
                                iconKey = summary.category.iconKey,
                                colorArgb = summary.category.colorArgb,
                                transactionCount = summary.transactionCount,
                            )
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: CategoriesAction) {
        when (action) {
            is CategoriesAction.TypeSelected -> {
                selectedType.value = action.type
                setState { copy(selectedType = action.type, isLoading = true) }
            }

            CategoriesAction.AddCategoryClicked -> sendEvent(CategoriesEvent.OpenEditor(null))

            is CategoriesAction.CategoryClicked -> sendEvent(CategoriesEvent.OpenEditor(action.id))

            is CategoriesAction.DeleteClicked -> {
                val row = currentState.categories.firstOrNull { it.id == action.id } ?: return
                setState {
                    copy(pendingDelete = PendingCategoryDelete(row.id, row.name, row.transactionCount))
                }
            }

            CategoriesAction.DeleteConfirmed -> {
                val pending = currentState.pendingDelete ?: return
                setState { copy(pendingDelete = null) }
                viewModelScope.launch {
                    when (val result = deleteCategory(pending.id)) {
                        is Outcome.Success -> sendEvent(
                            CategoriesEvent.ShowMessage(UiMessage.Plain("${pending.name} deleted")),
                        )

                        is Outcome.Failure -> sendEvent(
                            CategoriesEvent.ShowMessage(result.error.toUiMessage()),
                        )
                    }
                }
            }

            CategoriesAction.DeleteDismissed -> setState { copy(pendingDelete = null) }
        }
    }
}

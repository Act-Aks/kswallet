package com.kswallet.feature.categories.presentation.editor

import androidx.lifecycle.viewModelScope
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.categories.R
import com.kswallet.feature.categories.domain.usecase.ObserveCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.SaveCategoryResult
import com.kswallet.feature.categories.domain.usecase.SaveCategoryUseCase
import com.kswallet.feature.categories.domain.validation.CategoryFormError
import com.kswallet.feature.categories.domain.validation.CategoryFormInput
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Editor for a single category.
 *
 * The id arrives as a plain `Long` (0 = new) because Koin passes it as a parameter, and
 * `koinViewModel { parametersOf(...) }` keeps the graph free of "current id" globals.
 */
class CategoryEditorViewModel(
    categoryIdValue: Long,
    private val observeCategory: ObserveCategoryUseCase,
    private val saveCategory: SaveCategoryUseCase,
) : ScreenViewModel<CategoryEditorState, CategoryEditorAction, CategoryEditorEvent>(
    CategoryEditorState(isNew = categoryIdValue == 0L, isLoading = categoryIdValue != 0L),
) {

    private val categoryId = CategoryId(categoryIdValue)
    private var existing: Category? = null

    init {
        if (categoryId.isSaved) {
            observeCategory(categoryId)
                .onEach { loaded ->
                    if (loaded != null) {
                        existing = loaded
                        setState {
                            copy(
                                isLoading = false,
                                isNew = false,
                                name = loaded.name,
                                type = loaded.type,
                                iconKey = loaded.iconKey,
                                colorArgb = loaded.colorArgb,
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        } else {
            setState { copy(isLoading = false, colorArgb = DEFAULT_COLOR) }
        }
    }

    override fun onAction(action: CategoryEditorAction) {
        when (action) {
            is CategoryEditorAction.NameChanged -> setState { copy(name = action.name, error = null) }

            is CategoryEditorAction.TypeChanged -> setState { copy(type = action.type, error = null) }

            is CategoryEditorAction.IconChanged -> setState { copy(iconKey = action.iconKey) }

            is CategoryEditorAction.ColorChanged -> setState { copy(colorArgb = action.colorArgb) }

            CategoryEditorAction.BackClicked -> sendEvent(CategoryEditorEvent.NavigateBack())

            CategoryEditorAction.SaveClicked -> save()
        }
    }

    private fun save() {
        if (currentState.isSaving) return
        setState { copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val input = CategoryFormInput(
                name = currentState.name,
                type = currentState.type,
                iconKey = currentState.iconKey,
                colorArgb = currentState.colorArgb,
            )
            val result = saveCategory(input, existing)
            setState { copy(isSaving = false) }
            when (result) {
                is SaveCategoryResult.Saved -> {
                    sendEvent(CategoryEditorEvent.NavigateBack(savedOrDeleted = true))
                }

                is SaveCategoryResult.Invalid -> setState { copy(error = result.errors.first().toEditorError()) }

                is SaveCategoryResult.Failed -> sendEvent(
                    CategoryEditorEvent.ShowMessage(UiMessage.Resource(R.string.error_storage)),
                )
            }
        }
    }

    private fun CategoryFormError.toEditorError(): CategoryEditorError = when (this) {
        CategoryFormError.EmptyName -> CategoryEditorError.EmptyName
        CategoryFormError.NameTooLong -> CategoryEditorError.NameTooLong
        CategoryFormError.DuplicateName -> CategoryEditorError.DuplicateName
    }

    private companion object {
        /** Default accent for a brand-new category; the palette itself lives in the design system. */
        const val DEFAULT_COLOR: Long = 0xFF0E7C66
    }
}

package com.kswallet.feature.categories.presentation.list

import com.kswallet.core.model.CategoryId
import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface CategoriesEvent : ScreenEvent {
    data class OpenEditor(val categoryId: CategoryId?) : CategoriesEvent
    data class ShowMessage(val message: UiMessage) : CategoriesEvent
}

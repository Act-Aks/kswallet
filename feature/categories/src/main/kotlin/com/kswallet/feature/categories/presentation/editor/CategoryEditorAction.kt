package com.kswallet.feature.categories.presentation.editor

import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.screen.ScreenAction

sealed interface CategoryEditorAction : ScreenAction {
    data class NameChanged(val name: String) : CategoryEditorAction
    data class TypeChanged(val type: CategoryType) : CategoryEditorAction
    data class IconChanged(val iconKey: String) : CategoryEditorAction
    data class ColorChanged(val colorArgb: Long) : CategoryEditorAction
    data object SaveClicked : CategoryEditorAction
    data object BackClicked : CategoryEditorAction
}

package com.kswallet.feature.categories.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val categoriesNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(CategoriesRoute::class)
        subclass(CategoryEditorRoute::class)
    }
}

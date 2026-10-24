package com.kswallet.feature.categories.presentation.navigation

import com.kswallet.core.navigation.navigationSerializersModule

val categoriesNavigationSerializersModule = navigationSerializersModule(
    CategoriesRoute::class,
    CategoryEditorRoute::class,
)

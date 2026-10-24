package com.kswallet.feature.categories.presentation.navigation

import com.kswallet.core.navigation.KsWalletRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the categories feature. */
@Serializable
data object CategoriesRoute : KsWalletRoute

@Serializable
data class CategoryEditorRoute(val categoryId: Long? = null) : KsWalletRoute

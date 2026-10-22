plugins {
    alias(libs.plugins.kswallet.android.feature)
}

android {
    namespace = "com.kswallet.feature.budgets"
}

dependencies {
    // The budget editor picks a category, so it reads the categories *domain* layer
    // (`ObserveCategoriesUseCase`) - the only cross-feature import the layer rules allow.
    implementation(projects.feature.categories)
    // The template already brings in :core:testing (FixedTimeProvider, MainDispatcherRule) for
    // tests. The repository test also names Money/Currency/BudgetId and the database row types
    // directly, so it declares that dependency instead of leaning on the main variant's.
    testImplementation(projects.core.model)
}

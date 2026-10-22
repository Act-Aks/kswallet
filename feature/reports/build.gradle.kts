plugins {
    alias(libs.plugins.kswallet.android.feature)
}

android {
    namespace = "com.kswallet.feature.reports"
}

dependencies {
    // Reports owns its whole pipeline: it reads the ledger through its own DAO queries, so it
    // needs no other feature (unlike :feature:home, which aggregates two domains) and no
    // cross-feature import - which is also why it can query for statistics the ledger screen
    // has no use for (per-category and per-instant sums).
    testImplementation(projects.core.model)
}

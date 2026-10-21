plugins {
    alias(libs.plugins.kswallet.android.feature)
}

android {
    namespace = "com.kswallet.feature.accounts"
}

dependencies {
    // Accounts are a pure owner of their own data; nothing else is needed. Home and the
    // transaction editor depend on *this* module's domain layer, never the other way round.
}

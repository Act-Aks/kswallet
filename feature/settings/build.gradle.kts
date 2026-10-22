plugins {
    alias(libs.plugins.kswallet.android.feature)
}

android {
    namespace = "com.kswallet.feature.settings"
}

/*
 * No data layer and no `dependencies` block: the feature convention plugin already brings in
 * `:core:datastore`, and that module *is* this feature's data layer (DataStore), so there is
 * nothing left to add. A feature that owns no storage stays this small.
 */

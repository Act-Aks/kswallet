package com.kswallet

import com.kswallet.convention.configureKoin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Modules that declare Koin definitions (or start Koin) apply this convention.
 *
 * - runtime: koin-core / koin-android / koin-compose(-viewmodel) via the Koin BOM
 * - build time: the Koin compiler plugin validates the whole graph on `assemble*`
 */
class KsWalletAndroidDiPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        configureKoin()
    }
}

package com.kswallet.convention

import com.kswallet.tasks.VerifyLayerBoundariesTask
import org.gradle.api.Project

/**
 * Registers the boundary check for a module.
 *
 * Convention plugins stack (feature -> library.compose -> library), so this is idempotent:
 * the first registration wins, and a later caller can only make the check *stricter*.
 */
private fun Project.registerLayerBoundaryCheck(requireFrameworkFree: Boolean) {
    val existing = tasks.findByName(VerifyLayerBoundariesTask.TASK_NAME) as? VerifyLayerBoundariesTask
    if (existing != null) {
        if (requireFrameworkFree) existing.requireFrameworkFree.set(true)
        return
    }

    tasks.register(VerifyLayerBoundariesTask.TASK_NAME, VerifyLayerBoundariesTask::class.java) {
        group = "verification"
        description = "Fails the build when a source file breaks KsWallet's layer rules."
        sources.set(layout.projectDirectory.dir("src/main/kotlin"))
        this.requireFrameworkFree.set(requireFrameworkFree)
    }
}

internal fun Project.configureLayerBoundaryCheck(frameworkFree: Boolean = true) {
    registerLayerBoundaryCheck(requireFrameworkFree = frameworkFree)
    pluginManager.withPlugin("base") {
        tasks.named("check") { dependsOn(tasks.named(VerifyLayerBoundariesTask.TASK_NAME)) }
    }
}

package com.kswallet.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/**
 * Enforces the architectural rules that a single Gradle module per feature cannot
 * enforce by itself, because `data`, `domain` and `presentation` are packages here.
 *
 * Rules checked on every `import`:
 *  1. `domain` never depends on `data`/`presentation`, Android, Compose or a DI framework.
 *  2. `data` never depends on `presentation`.
 *  3. `presentation` never depends on `data` (it talks to `domain` only).
 *  4. A feature never reaches into another feature's `data`/`presentation`
 *     (cross-feature dependencies may only target `domain`, or `presentation.navigation`
 *     which exposes destination contracts).
 *  5. Modules registered as framework-free (`:core:common`, `:core:model`, `:core:testing`)
 *     never import Android, AndroidX or Koin at all.
 *
 * One deliberate exemption: a `<layer>/di/` package is *composition wiring*, so the
 * framework rules (Android/AndroidX/DI) do not apply to it. `domain/di/AccountsDomainModule`
 * is allowed to know Koin - the rule that matters is that no `domain` *logic* file does, and
 * every inter-layer rule still applies inside `di/` too.
 *
 * Violations fail `check`, so the rules are impossible to forget.
 */
@CacheableTask
abstract class VerifyLayerBoundariesTask : DefaultTask() {

    companion object {
        const val TASK_NAME = "verifyLayerBoundaries"
    }

    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: DirectoryProperty

    /** When true the whole module must stay pure Kotlin (no Android/AndroidX/DI imports). */
    @get:Input
    abstract val requireFrameworkFree: Property<Boolean>

    @TaskAction
    fun verify() {
        val rootDir = sources.get().asFile
        if (!rootDir.exists()) return

        val violations = mutableListOf<String>()

        rootDir.walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { file ->
            val relativePath = file.relativeTo(rootDir).invariantSeparatorsPath
            val segments = relativePath.split('/')
            val featureName = segments.getFeatureName()
            val layer = segments.getLayer()
            val frameworkFree = requireFrameworkFree.getOrElse(false)
            // `<layer>/di/**` is wiring, not logic: it may name the DI framework.
            val wiring = segments.contains("di")

            file.useLines { lines ->
                lines.filter { it.trimStart().startsWith("import ") }.forEach { rawLine ->
                    val imported = rawLine.trim().removePrefix("import ").trim()
                    val violation = when {
                        frameworkFree -> imported.frameworkViolation()
                        layer == "domain" -> imported.domainViolation(featureName, wiring)
                        layer == "data" -> imported.dataViolation(featureName)
                        layer == "presentation" -> imported.presentationViolation(featureName)
                        else -> imported.crossFeatureViolation(featureName)
                    }
                    if (violation != null) {
                        violations += "$relativePath\n      -> $violation\n      (import $imported)"
                    }
                }
            }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("KsWallet layer boundary violations found in ${project.path} (${violations.size}):")
                    appendLine()
                    violations.sorted().forEach { appendLine("  • $it") }
                    appendLine()
                    appendLine("See docs/ARCHITECTURE.md -> 'Dependency rules enforced by the build'.")
                },
            )
        }
    }

    private fun List<String>.getFeatureName(): String? {
        val index = indexOf("feature")
        return if (index >= 0 && size > index + 1) this[index + 1] else null
    }

    private fun List<String>.getLayer(): String? =
        firstOrNull { it == "data" || it == "domain" || it == "presentation" }

    private fun String.isAndroid(): Boolean = startsWith("android.") || startsWith("androidx.")
    private fun String.isDi(): Boolean = startsWith("io.insert-koin.") || startsWith("org.koin.")
    private fun String.isKswallet(): Boolean = startsWith("com.kswallet.")

    /** Data-only modules a feature layer must never reach into directly. */
    private fun String.isDataOnlyModule(): Boolean =
        startsWith("com.kswallet.core.database.") || startsWith("com.kswallet.core.datastore.data.")

    private fun String.isData(): Boolean =
        isKswallet() && (contains(".data.") || endsWith(".data") || isDataOnlyModule())

    private fun String.isPresentation(): Boolean =
        isKswallet() && (contains(".presentation.") || endsWith(".presentation"))

    private fun String.isDestinationContract(): Boolean = contains(".presentation.navigation.")

    private fun String.frameworkViolation(): String? = when {
        isAndroid() -> "must stay pure Kotlin (Android/AndroidX import)"
        isDi() -> "must stay pure Kotlin (DI framework import)"
        else -> null
    }

    private fun String.domainViolation(feature: String?, wiring: Boolean): String? = when {
        wiring -> null
        isAndroid() -> "domain must not depend on Android/AndroidX"
        isDi() -> "domain must not depend on a DI framework"
        isData() -> "domain must not depend on the data layer"
        isPresentation() -> "domain must not depend on the presentation layer"
        feature != null && this.isForeignFeature(feature) && !this.contains(".domain.") -> "domain may only use another feature's domain layer"

        else -> null
    }

    private fun String.dataViolation(feature: String?): String? = when {
        isPresentation() -> "data must not depend on the presentation layer"
        feature != null && this.isForeignFeature(feature) -> "data may only use another feature's domain layer (not its data layer)"

        else -> null
    }

    private fun String.presentationViolation(feature: String?): String? = when {
        isData() -> "presentation must talk to the domain layer, never to data"
        feature != null && this.isForeignFeature(feature) && !this.contains(".domain.") && !isDestinationContract() -> "presentation may only use another feature's domain layer or its navigation contract"

        else -> null
    }

    private fun String.crossFeatureViolation(feature: String?): String? =
        if (feature != null && this.isForeignFeature(feature) && (isData() || isPresentation()) && !isDestinationContract()) {
            "only the domain layer and navigation contracts may be shared between features"
        } else {
            null
        }

    private fun String.isForeignFeature(feature: String): Boolean {
        if (!startsWith("com.kswallet.feature.")) return false
        val other = substringAfter("com.kswallet.feature.").substringBefore('.')
        return other != feature
    }
}

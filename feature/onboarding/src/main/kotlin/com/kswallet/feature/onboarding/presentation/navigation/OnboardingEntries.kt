package com.kswallet.feature.onboarding.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.onboarding.presentation.onboarding.OnboardingRoot

fun EntryProviderScope<NavKey>.onboardingEntries(
    onCompleted: () -> Unit,
) {
    entry<OnboardingRoute> {
        OnboardingRoot(onCompleted = onCompleted)
    }
}

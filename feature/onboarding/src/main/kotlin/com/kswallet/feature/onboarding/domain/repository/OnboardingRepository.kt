package com.kswallet.feature.onboarding.domain.repository

import com.kswallet.feature.onboarding.domain.model.OnboardingPreferences

interface OnboardingRepository {
    /** Persist the selected preferences and mark first-run setup complete. */
    suspend fun completeOnboarding(preferences: OnboardingPreferences)
}

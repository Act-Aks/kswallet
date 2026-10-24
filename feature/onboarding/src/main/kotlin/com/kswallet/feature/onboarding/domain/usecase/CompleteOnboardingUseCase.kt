package com.kswallet.feature.onboarding.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.feature.onboarding.domain.model.OnboardingPreferences
import com.kswallet.feature.onboarding.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(preferences: OnboardingPreferences): Outcome<Unit> =
        outcomeOfSuspend { repository.completeOnboarding(preferences) }
}

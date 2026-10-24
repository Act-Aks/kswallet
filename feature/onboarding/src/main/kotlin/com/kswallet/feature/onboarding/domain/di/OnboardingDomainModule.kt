package com.kswallet.feature.onboarding.domain.di

import com.kswallet.feature.onboarding.domain.repository.OnboardingRepository
import com.kswallet.feature.onboarding.domain.usecase.CompleteOnboardingUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class OnboardingDomainModule {
    @Factory
    fun provideCompleteOnboardingUseCase(
        repository: OnboardingRepository,
    ): CompleteOnboardingUseCase = CompleteOnboardingUseCase(repository)
}

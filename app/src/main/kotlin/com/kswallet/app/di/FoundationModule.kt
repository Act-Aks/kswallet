package com.kswallet.app.di

import com.kswallet.app.foundation.FoundationCounterPolicy
import com.kswallet.app.foundation.FoundationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foundationModule = module {
    singleOf(::FoundationCounterPolicy)
    viewModelOf(::FoundationViewModel)
}
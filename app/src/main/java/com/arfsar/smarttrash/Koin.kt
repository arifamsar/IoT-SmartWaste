package com.arfsar.smarttrash

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SensorRepository() }
    viewModel { SensorViewModel(get()) }
}
package com.nha.uangmasukkeluar.di

import com.nha.uangmasukkeluar.ui.finance.FinanceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        FinanceViewModel(get())
    }
}
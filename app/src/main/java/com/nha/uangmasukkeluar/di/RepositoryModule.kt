package com.nha.uangmasukkeluar.di

import com.nha.uangmasukkeluar.data.repository.FinanceRepository
import com.nha.uangmasukkeluar.data.repository.FinanceRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<FinanceRepository> {
        FinanceRepositoryImpl(get())
    }
}
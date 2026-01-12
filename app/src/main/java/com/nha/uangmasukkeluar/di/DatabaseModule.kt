package com.nha.uangmasukkeluar.di

import androidx.room.Room
import com.nha.uangmasukkeluar.data.local.FinanceDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            FinanceDatabase::class.java,
            "app_db"
        ).build()
    }

    single { get<FinanceDatabase>().financeDao() }
}
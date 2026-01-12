package com.nha.uangmasukkeluar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nha.uangmasukkeluar.data.local.entity.FinanceInEntity
import com.nha.uangmasukkeluar.data.local.entity.FinanceOutEntity

@Database(
    entities = [
        FinanceInEntity::class,
        FinanceOutEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao
}

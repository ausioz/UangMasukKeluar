package com.nha.uangmasukkeluar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finance_out")
data class FinanceOutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateTime: String,
    val masukKe: String,
    val terimaDari: String,
    val keterangan: String,
    val jumlah: Int,
    val jenisPendapatan: String,
    val buktiUri: String
)
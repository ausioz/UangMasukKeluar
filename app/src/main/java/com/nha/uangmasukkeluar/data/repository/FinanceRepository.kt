package com.nha.uangmasukkeluar.data.repository

import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.domain.model.FinanceOut
import kotlinx.coroutines.flow.Flow


interface FinanceRepository {

    fun getFinanceIn(): Flow<List<FinanceIn>>
    fun getFinanceOut(): Flow<List<FinanceOut>>

    suspend fun addFinanceIn(
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    )

    suspend fun updateFinanceIn(
        id: Int,
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    )

    suspend fun deleteFinanceIn(id: Int)

    suspend fun addFinanceOut(
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    )

    suspend fun deleteFinanceOut(id: Int)
}

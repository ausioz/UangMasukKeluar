package com.nha.uangmasukkeluar.data.repository

import com.nha.uangmasukkeluar.data.local.FinanceDao
import com.nha.uangmasukkeluar.data.local.entity.FinanceInEntity
import com.nha.uangmasukkeluar.data.local.entity.FinanceOutEntity
import com.nha.uangmasukkeluar.data.mapper.toDomain
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.domain.model.FinanceOut
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinanceRepositoryImpl(
    private val dao: FinanceDao
) : FinanceRepository {

    override fun getFinanceIn(): Flow<List<FinanceIn>> = dao.getFinanceIn().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun addFinanceIn(
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    ) {
        dao.insertFinanceIn(
            FinanceInEntity(
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
        )
    }

    override suspend fun updateFinanceIn(
        id: Int,
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    ) {
        dao.updateFinanceIn(
            FinanceInEntity(
                id = id,
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
        )
    }

    override suspend fun deleteFinanceIn(id: Int) {
        dao.deleteFinanceIn(id)
    }

    override fun getFinanceOut(): Flow<List<FinanceOut>> = dao.getFinanceOut().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun addFinanceOut(
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    ) {
        dao.insertFinanceOut(
            FinanceOutEntity(
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
        )
    }

    override suspend fun deleteFinanceOut(id: Int) {
        dao.deleteFinanceOut(id)
    }
}


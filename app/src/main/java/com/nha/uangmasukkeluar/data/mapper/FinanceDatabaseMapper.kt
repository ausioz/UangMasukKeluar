package com.nha.uangmasukkeluar.data.mapper

import com.nha.uangmasukkeluar.data.local.entity.FinanceInEntity
import com.nha.uangmasukkeluar.data.local.entity.FinanceOutEntity
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.domain.model.FinanceOut

fun FinanceInEntity.toDomain() =
    FinanceIn(id, dateTime, masukKe, terimaDari, keterangan, jumlah, jenisPendapatan, buktiUri)

fun FinanceOutEntity.toDomain() =
    FinanceOut(id, dateTime, masukKe, terimaDari, keterangan, jumlah, jenisPendapatan, buktiUri)

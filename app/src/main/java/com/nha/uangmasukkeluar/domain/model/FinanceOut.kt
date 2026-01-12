package com.nha.uangmasukkeluar.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinanceOut(
    val id: Int = 0,
    val dateTime: String,
    val masukKe: String,
    val terimaDari: String,
    val keterangan: String,
    val jumlah: Int,
    val jenisPendapatan: String,
    val buktiUri: String
) : Parcelable

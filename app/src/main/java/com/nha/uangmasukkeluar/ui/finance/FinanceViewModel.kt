package com.nha.uangmasukkeluar.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nha.uangmasukkeluar.data.repository.FinanceRepository
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.domain.model.FinanceOut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    val financeIn: StateFlow<List<FinanceIn>> = repository.getFinanceIn().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val financeOut: StateFlow<List<FinanceOut>> = repository.getFinanceOut().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun addFinanceIn(
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    ) {
        viewModelScope.launch {
            repository.addFinanceIn(
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
        }
    }

    fun updateFinanceIn(
        id: Int,
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    ) {
        viewModelScope.launch {
            repository.updateFinanceIn(
                id = id,
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
        }
    }

    fun deleteFinanceIn(id: Int) {
        viewModelScope.launch {
            repository.deleteFinanceIn(id)
        }
    }

    fun addFinanceOut(
        dateTime: String,
        masukKe: String,
        terimaDari: String,
        keterangan: String,
        jumlah: Int,
        jenisPendapatan: String,
        buktiUri: String
    ) {
        viewModelScope.launch {
            repository.addFinanceOut(
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
        }
    }

    fun deleteFinanceOut(id: Int) {
        viewModelScope.launch {
            repository.deleteFinanceOut(id)
        }
    }
}
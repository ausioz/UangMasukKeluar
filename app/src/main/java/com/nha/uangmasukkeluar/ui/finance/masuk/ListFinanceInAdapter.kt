package com.nha.uangmasukkeluar.ui.finance.masuk

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nha.uangmasukkeluar.databinding.ItemFinanceByDateBinding
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import java.text.SimpleDateFormat
import java.util.Locale

class FinanceInAdapter(
    private val onItemClick: (FinanceIn) -> Unit
) : ListAdapter<List<FinanceIn>, FinanceInAdapter.ViewHolder>(ListFinanceInDiffCallback()) {

    private var groupedData: List<List<FinanceIn>> = emptyList()

    fun submitFlatList(list: List<FinanceIn>) {
        groupedData = groupFinanceInByDate(list)
        submitList(groupedData)
    }

    private fun groupFinanceInByDate(list: List<FinanceIn>): List<List<FinanceIn>> {
        return list.groupBy { extractDate(it.dateTime) }.values.toList()
    }

    private fun extractDate(dateTime: String): String {
        return try {
            val parts = dateTime.split(" ")
            if (parts.isNotEmpty()) parts[0] else dateTime
        } catch (_: Exception) {
            dateTime
        }
    }

    inner class ViewHolder(private val binding: ItemFinanceByDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val childAdapter = FinanceInDetailAdapter(onItemClick)

        init {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = childAdapter
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(items: List<FinanceIn>) {
            childAdapter.submitList(items)

            if (items.isNotEmpty()) {
                val firstItem = items.first()
                binding.tvTanggal.text = formatDate(firstItem.dateTime)
                val total = items.sumOf { it.jumlah }
                binding.tvTotal.text = "Rp ${formatCurrency(total)}"
            }

            binding.root.setOnClickListener {
                if (items.isNotEmpty()) {
                    onItemClick(items.first())
                }
            }
        }

        private fun formatDate(dateTime: String): String {
            return try {
                val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", localeBuilder)
                val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", localeBuilder)
                val date = inputFormat.parse(dateTime)
                date?.let { outputFormat.format(it) } ?: dateTime
            } catch (_: Exception) {
                dateTime
            }
        }

        @SuppressLint("DefaultLocale")
        private fun formatCurrency(amount: Int): String {
            return String.format("%,d", amount).replace(",", ".")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFinanceByDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ListFinanceInDiffCallback : DiffUtil.ItemCallback<List<FinanceIn>>() {
    override fun areItemsTheSame(oldItem: List<FinanceIn>, newItem: List<FinanceIn>): Boolean {
        if (oldItem.isEmpty() || newItem.isEmpty()) return false
        return oldItem.first().id == newItem.first().id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: List<FinanceIn>, newItem: List<FinanceIn>): Boolean {
        return oldItem == newItem
    }
}
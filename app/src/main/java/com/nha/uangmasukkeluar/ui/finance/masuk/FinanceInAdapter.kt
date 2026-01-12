package com.nha.uangmasukkeluar.ui.finance.masuk

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nha.uangmasukkeluar.databinding.ItemFinanceDetailBinding
import com.nha.uangmasukkeluar.domain.model.FinanceIn

class FinanceInDetailAdapter(
    private val onItemClick: (FinanceIn) -> Unit
) : ListAdapter<FinanceIn, FinanceInDetailAdapter.DetailViewHolder>(FinanceInDiffCallback()) {

    inner class DetailViewHolder(private val binding: ItemFinanceDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FinanceIn) {
            binding.tvTime.text = extractTime(item.dateTime)
            binding.tvTerimaDari.text = item.terimaDari
            binding.tvTotal.text = "Rp ${formatCurrency(item.jumlah)}"
            binding.tvMasukKe.text = item.masukKe
            binding.tvKeterangan.text = item.keterangan

            binding.root.setOnClickListener { onItemClick(item) }
        }

        private fun extractTime(dateTime: String): String {
            return try {
                val parts = dateTime.split(" ")
                if (parts.size >= 2) parts[1] else dateTime
            } catch (_: Exception) {
                dateTime
            }
        }

        @SuppressLint("DefaultLocale")
        private fun formatCurrency(amount: Int): String {
            return String.format("%,d", amount).replace(",", ".")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemFinanceDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class FinanceInDiffCallback : DiffUtil.ItemCallback<FinanceIn>() {
    override fun areItemsTheSame(oldItem: FinanceIn, newItem: FinanceIn): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FinanceIn, newItem: FinanceIn): Boolean {
        return oldItem == newItem
    }
}
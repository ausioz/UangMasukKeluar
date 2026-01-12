package com.nha.uangmasukkeluar.ui.finance.modal.pelanggan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nha.uangmasukkeluar.databinding.ItemPelangganBinding
import com.nha.uangmasukkeluar.domain.model.Pelanggan

class PelangganAdapter(
    private val onItemClick: (Pelanggan) -> Unit
) : ListAdapter<Pelanggan, PelangganAdapter.ViewHolder>(PelangganDiffCallback()) {

    inner class ViewHolder(private val binding: ItemPelangganBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pelanggan: Pelanggan) {
            binding.tvNama.text = pelanggan.nama

            if (pelanggan.telepon != null) {
                binding.tvTelepon.text = pelanggan.telepon
                binding.tvTelepon.visibility = View.VISIBLE
            } else {
                binding.tvTelepon.visibility = View.GONE
            }

            if (pelanggan.email != null) {
                binding.tvEmail.text = pelanggan.email
                binding.tvEmail.visibility = View.VISIBLE
            } else {
                binding.tvEmail.visibility = View.GONE
            }

            val firstLetter =
                pelanggan.nama.takeIf { it.isNotEmpty() }?.firstOrNull()?.uppercaseChar()
                    ?.toString() ?: "-"
            binding.letterCircle.text = firstLetter

            if (pelanggan.isMember == true) {
                binding.tvIsMember.visibility = View.VISIBLE
            } else {
                binding.tvIsMember.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(pelanggan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPelangganBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PelangganDiffCallback : DiffUtil.ItemCallback<Pelanggan>() {
    override fun areItemsTheSame(oldItem: Pelanggan, newItem: Pelanggan): Boolean {
        return oldItem.nama == newItem.nama
    }

    override fun areContentsTheSame(oldItem: Pelanggan, newItem: Pelanggan): Boolean {
        return oldItem == newItem
    }
}
package com.nha.uangmasukkeluar.ui.finance.modal.pelanggan

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nha.uangmasukkeluar.MyApp
import com.nha.uangmasukkeluar.databinding.FragmentDialogCariPelangganBinding
import com.nha.uangmasukkeluar.domain.model.Pelanggan
import com.nha.uangmasukkeluar.domain.model.dummyList

class CariPelangganDialogFragment(private val onPelangganSelected: ((Pelanggan) -> Unit)? = null) :
    DialogFragment() {

    private var _binding: FragmentDialogCariPelangganBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PelangganAdapter
    private val pelangganList: List<Pelanggan> = dummyList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogCariPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        if (MyApp.isTablet(requireContext())) {
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.6f).toInt()

            dialog?.window?.setLayout(width, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            dialog?.window?.setDimAmount(0f)
            dialog?.window?.setGravity(Gravity.CENTER)
        } else {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog?.window?.setBackgroundDrawable(Color.WHITE.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBar.setNavigationOnClickListener {
            dismiss()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilSearch.hint = ""
            } else {
                binding.tilSearch.hint = "Cari Pelanggan Disini"
            }
        }

        setupRecyclerView()
        setupSearch()

        binding.btTambah.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        adapter = PelangganAdapter { pelanggan ->
            onPelangganSelected?.invoke(pelanggan)
            dismiss()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CariPelangganDialogFragment.adapter
        }

        adapter.submitList(pelangganList)
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { s ->
            val query = s.toString().trim().lowercase()
            val filteredList = pelangganList.filter { pelanggan ->
                pelanggan.nama.lowercase().contains(query) || pelanggan.telepon?.lowercase()
                    ?.contains(query) == true || pelanggan.email?.lowercase()
                    ?.contains(query) == true
            }
            adapter.submitList(filteredList)
        }
    }

}
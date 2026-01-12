package com.nha.uangmasukkeluar.ui.finance.modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nha.uangmasukkeluar.databinding.LayoutBottomSheetJenisPendapatanBinding

class JenisPendapatanBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: LayoutBottomSheetJenisPendapatanBinding? = null
    private val binding get() = _binding!!

    var onPendapatanLainSelected: (() -> Unit)? = null
    var onNonPendapatanSelected: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LayoutBottomSheetJenisPendapatanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.rbPendapatanLain.setOnClickListener {
            onPendapatanLainSelected?.invoke()
            dismiss()
        }

        binding.rbNonPendapatan.setOnClickListener {
            onNonPendapatanSelected?.invoke()
            dismiss()
        }
    }

    companion object {
        const val JENIS_PENDAPATAN_LAIN = "Pendapatan Lain"
        const val JENIS_NON_PENDAPATAN = "Non Pendapatan"
    }
}
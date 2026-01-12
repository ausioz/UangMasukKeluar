package com.nha.uangmasukkeluar.ui.finance.modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nha.uangmasukkeluar.databinding.LayoutBottomSheetUploadImageBinding

class UploadImageBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: LayoutBottomSheetUploadImageBinding? = null
    private val binding get() = _binding!!

    var onGallerySelected: (() -> Unit)? = null
    var onCameraSelected: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutBottomSheetUploadImageBinding.inflate(inflater, container, false)
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

        binding.clGaleri.setOnClickListener {
            onGallerySelected?.invoke()
            dismiss()
        }

        binding.clKamera.setOnClickListener {
            onCameraSelected?.invoke()
            dismiss()
        }
    }

}
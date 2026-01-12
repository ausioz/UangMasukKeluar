package com.nha.uangmasukkeluar.ui.finance.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nha.uangmasukkeluar.MyApp
import com.nha.uangmasukkeluar.R
import com.nha.uangmasukkeluar.databinding.DialogDeleteConfirmationBinding
import com.nha.uangmasukkeluar.databinding.DialogZoomableImageBinding
import com.nha.uangmasukkeluar.databinding.FragmentUangMasukDetailBinding
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.ui.finance.FinanceActivity
import com.nha.uangmasukkeluar.ui.finance.FinanceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailUangFragment : Fragment() {

    private var _binding: FragmentUangMasukDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var financeIn: FinanceIn
    private val financeViewModel: FinanceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUangMasukDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isInDetailContainer()) {
            (activity as FinanceActivity).showLLMenu()
        }
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Detail Uang Masuk"

        @Suppress("DEPRECATION")
        financeIn = arguments?.getParcelable("financeIn") ?: return

        populateData()
        setupButtons()
        setupOnBackPressed()
    }

    private fun setupButtons() {
        binding.btEdit.setOnClickListener {
            navigateToEdit()
        }

        binding.tvDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.ivClose?.setOnClickListener {
            (activity as FinanceActivity).showLLMenu()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun navigateToEdit() {
        val bundle = Bundle().apply {
            putParcelable("financeIn", financeIn)
        }

        if (isInDetailContainer()) {
            (activity as FinanceActivity).showLLMenu()

            val navHostFragment = requireActivity().supportFragmentManager
                .findFragmentById(R.id.fragmentContainer) as NavHostFragment
            navHostFragment.navController.navigate(
                R.id.to_uangMasukBuatTransaksiFragment, bundle
            )
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            findNavController().navigate(
                R.id.to_uangMasukBuatTransaksiFragment, bundle
            )
        }
    }

    private fun showDeleteConfirmationDialog() {
        val binding = DialogDeleteConfirmationBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).setView(binding.root)
            .setCancelable(true)
            .create()

        binding.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.btDelete.setOnClickListener {
            deleteTransaction()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun deleteTransaction() {
        financeViewModel.deleteFinanceIn(financeIn.id)
        Toast.makeText(requireContext(), "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()

        if (isInDetailContainer()) {
            (activity as FinanceActivity).showLLMenu()
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            findNavController().popBackStack(R.id.uangMasukMainFragment, false)
        }
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isInDetailContainer()) {
                (activity as FinanceActivity).showLLMenu()

                requireActivity().supportFragmentManager.popBackStack()
            } else {
                findNavController().popBackStack(R.id.uangMasukMainFragment, false)
            }
        }
    }

    private fun isInDetailContainer(): Boolean {
        return requireActivity().supportFragmentManager.findFragmentById(R.id.detail_container) == this
    }

    @SuppressLint("SetTextI18n")
    private fun populateData() {
        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", localeBuilder)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy - HH:mm:ss", localeBuilder)
        try {
            val date = inputFormat.parse(financeIn.dateTime)
            date?.let {
                binding.tvDateTimeValue.text = outputFormat.format(it)
            }
        } catch (_: Exception) {
            binding.tvDateTimeValue.text = financeIn.dateTime
        }

        binding.tvMasukKeValue.text = financeIn.masukKe
        binding.tvTerimaDariValue.text = financeIn.terimaDari
        binding.tvKeteranganValue.text = financeIn.keterangan
        binding.tvJumlahValue.text = "Rp ${formatCurrency(financeIn.jumlah)}"
        binding.tvJenisValue.text = financeIn.jenisPendapatan

        if (financeIn.buktiUri.isNotEmpty()) {
            val imageUri = financeIn.buktiUri.toUri()
            Glide.with(requireContext()).load(imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background).into(binding.ivUpload)

            binding.ivUpload.setOnClickListener {
                showZoomableImageDialog(financeIn.buktiUri)
            }
        } else {
            binding.ivUpload.visibility = View.GONE
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatCurrency(amount: Int): String {
        return String.format("%,d", amount).replace(",", ".")
    }

    private fun showZoomableImageDialog(imageUri: String) {
        val binding = DialogZoomableImageBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).setView(binding.root)
            .setCancelable(true)
            .create()
        dialog.show()

        if (MyApp.isTablet(requireContext())){
            dialog.window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundDrawableResource(android.R.color.black)
            }
        }

        Glide.with(requireContext()).load(imageUri.toUri())
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.photoView)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

    }
}
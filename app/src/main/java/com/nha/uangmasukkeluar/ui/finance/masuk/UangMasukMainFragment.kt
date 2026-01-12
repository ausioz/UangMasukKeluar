package com.nha.uangmasukkeluar.ui.finance.masuk

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.nha.uangmasukkeluar.MyApp
import com.nha.uangmasukkeluar.R
import com.nha.uangmasukkeluar.databinding.FragmentUangMasukMainBinding
import com.nha.uangmasukkeluar.databinding.LayoutBottomSheetPeriodBinding
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.ui.finance.FinanceActivity
import com.nha.uangmasukkeluar.ui.finance.FinanceViewModel
import com.nha.uangmasukkeluar.ui.finance.detail.DetailUangFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UangMasukMainFragment : Fragment() {

    private var _binding: FragmentUangMasukMainBinding? = null
    private val binding get() = _binding!!

    private val financeViewModel: FinanceViewModel by viewModel()

    private lateinit var financeInAdapter: FinanceInAdapter

    private var startDate: Long = 0L
    private var endDate: Long = 0L
    private var selectedDateRangeMode = DateRangeMode.TODAY
    private var startDateTemporal: Long = 0L
    private var endDateTemporal: Long = 0L
    private var currentFinanceInList: List<FinanceIn> = emptyList()

    private enum class DateRangeMode {
        TODAY, YESTERDAY, LAST_7_DAYS, CUSTOM_RANGE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUangMasukMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Uang Masuk"

        initializeDateRange()
        setupAdapter()
        observeFinanceIn()
        initUi()
    }

    private fun initializeDateRange() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        startDate = calendar.timeInMillis
        endDate = System.currentTimeMillis()

        updatePeriodeText()
    }

    private fun setupAdapter() {
        financeInAdapter = FinanceInAdapter { financeIn ->
            navigateToDetail(financeIn)
        }
        binding.recyclerView.adapter = financeInAdapter
    }

    private fun observeFinanceIn() {
        viewLifecycleOwner.lifecycleScope.launch {
            financeViewModel.financeIn.collect { financeInList ->
                currentFinanceInList = financeInList
                val filteredList = filterByDateRange(financeInList)
                financeInAdapter.submitFlatList(filteredList)
                binding.llNoData.isVisible = filteredList.isEmpty()
                binding.recyclerView.isVisible = filteredList.isNotEmpty()
            }
        }
    }

    private fun filterByDateRange(list: List<FinanceIn>): List<FinanceIn> {
        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", localeBuilder)

        return list.filter { financeIn ->
            try {
                val itemDate = inputFormat.parse(financeIn.dateTime)?.time ?: 0L
                itemDate in startDate..endDate
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun initUi() {
        if (MyApp.isTablet(requireContext())) {
            (requireActivity() as FinanceActivity).hideToolbar()
        }

        binding.tvPeriode.setOnClickListener {
            if (MyApp.isTablet(requireContext())) {
                showCustomDatePickerLandscape()
            } else {
                showBottomSheetPeriode()
            }
        }

        binding.btAturPeriode.setOnClickListener {
            if (MyApp.isTablet(requireContext())) {
                showCustomDatePickerLandscape()
            } else {
                showBottomSheetPeriode()
            }
        }

        binding.btInsertuangMasuk.setOnClickListener {
            (requireActivity() as FinanceActivity).clearDetailContainer()
            findNavController().navigate(R.id.to_uangMasukBuatTransaksiFragment)
        }
    }

    private fun showBottomSheetPeriode() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.bottomSheetDialogStyle)
        val bindingBottomSheet = LayoutBottomSheetPeriodBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.root)

        when (selectedDateRangeMode) {
            DateRangeMode.TODAY -> bindingBottomSheet.rbHariIni.isChecked = true
            DateRangeMode.YESTERDAY -> bindingBottomSheet.rbKemarin.isChecked = true
            DateRangeMode.LAST_7_DAYS -> bindingBottomSheet.rb7HariTerakhir.isChecked = true
            DateRangeMode.CUSTOM_RANGE -> {
                bindingBottomSheet.rbPilihTgl.isChecked = true
                bindingBottomSheet.tilPilihTgl.isVisible = true
                bindingBottomSheet.etPilihTgl.setText(binding.tvPeriode.text)
            }
        }

        bindingBottomSheet.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.rgTgl.setOnCheckedChangeListener { _, checkedId ->
            bindingBottomSheet.tilPilihTgl.isVisible = checkedId == bindingBottomSheet.rbPilihTgl.id
        }

        bindingBottomSheet.etPilihTgl.setOnClickListener {
            showCustomDatePicker(bindingBottomSheet)
        }

        bindingBottomSheet.btTerapkan.setOnClickListener {
            applyDateFilter(bindingBottomSheet)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun applyDateFilter(bindingBottomSheet: LayoutBottomSheetPeriodBinding) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (bindingBottomSheet.rgTgl.checkedRadioButtonId) {
            bindingBottomSheet.rbHariIni.id -> {
                selectedDateRangeMode = DateRangeMode.TODAY
                startDate = calendar.timeInMillis
                endDate = System.currentTimeMillis()
            }

            bindingBottomSheet.rbKemarin.id -> {
                selectedDateRangeMode = DateRangeMode.YESTERDAY
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val yesterdayStart = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val yesterdayEnd = calendar.timeInMillis
                startDate = yesterdayStart
                endDate = yesterdayEnd
            }

            bindingBottomSheet.rb7HariTerakhir.id -> {
                selectedDateRangeMode = DateRangeMode.LAST_7_DAYS
                calendar.add(Calendar.DAY_OF_MONTH, -6)
                startDate = calendar.timeInMillis
                endDate = System.currentTimeMillis()
            }

            bindingBottomSheet.rbPilihTgl.id -> {
                selectedDateRangeMode = DateRangeMode.CUSTOM_RANGE
                startDate = startDateTemporal
                endDate = endDateTemporal
            }
        }

        updatePeriodeText()
        onDateRangeChanged()
    }

    private fun onDateRangeChanged() {
        val filteredList = filterByDateRange(currentFinanceInList)
        financeInAdapter.submitFlatList(filteredList)
        binding.llNoData.isVisible = filteredList.isEmpty()
        binding.recyclerView.isVisible = filteredList.isNotEmpty()
        binding.llTableTitle?.isVisible = filteredList.isNotEmpty()
        if (!MyApp.isTablet(requireContext())) binding.tvPeriode.isVisible = filteredList.isNotEmpty()
    }

    @SuppressLint("SetTextI18n")
    private fun updatePeriodeText() {
        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()
        val sdf = SimpleDateFormat("dd MMM yyyy", localeBuilder)

        val startStr = sdf.format(Date(startDate))
        val endStr = sdf.format(Date(endDate))

        binding.tvPeriode.text = "$startStr - $endStr"
    }

    private fun showCustomDatePickerLandscape() {
        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()

        val oldLocale = Locale.getDefault()
        Locale.setDefault(localeBuilder)

        val config = resources.configuration
        config.setLocale(localeBuilder)
        @Suppress("DEPRECATION") resources.updateConfiguration(config, resources.displayMetrics)

        val picker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Pilih Tanggal")
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR).setPositiveButtonText("Terapkan")
            .setNegativeButtonText("Batal").build()

        picker.addOnPositiveButtonClickListener { selection ->
            binding.tvPeriode.text = handleDateRangeSelection(selection)
            selectedDateRangeMode = DateRangeMode.CUSTOM_RANGE
            startDate = startDateTemporal
            endDate = endDateTemporal
            onDateRangeChanged()
        }

        picker.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                val header =
                    picker.view?.findViewById<View>(com.google.android.material.R.id.mtrl_picker_header)
                val mainPane =
                    picker.view?.findViewById<View>(com.google.android.material.R.id.mtrl_calendar_main_pane)

                header?.setBackgroundColor(Color.WHITE)
                mainPane?.setBackgroundColor(Color.WHITE)
            }
        })

        picker.addOnDismissListener {
            Locale.setDefault(oldLocale)
            val oldConfig = resources.configuration
            oldConfig.setLocale(oldLocale)
            @Suppress("DEPRECATION") resources.updateConfiguration(
                oldConfig, resources.displayMetrics
            )
        }

        picker.show(parentFragmentManager, "showCustomDatePicker")
    }

    @SuppressLint("SetTextI18n")
    private fun showCustomDatePicker(
        bindingBottomSheet: LayoutBottomSheetPeriodBinding,
    ) {
        if (parentFragmentManager.findFragmentByTag("showCustomDatePicker") != null) return

        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()

        val oldLocale = Locale.getDefault()
        Locale.setDefault(localeBuilder)

        val config = resources.configuration
        config.setLocale(localeBuilder)
        @Suppress("DEPRECATION") resources.updateConfiguration(config, resources.displayMetrics)

        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Tanggal awal - Tanggal Akhir")
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR).setPositiveButtonText("Terapkan")
            .setNegativeButtonText("Batal").build()

        picker.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                val header =
                    picker.view?.findViewById<View>(com.google.android.material.R.id.fullscreen_header)
                val pickerContainer =
                    picker.view?.findViewById<View>(com.google.android.material.R.id.mtrl_picker_fullscreen)

                val toggleBtn =
                    picker.view?.findViewById<ImageButton>(com.google.android.material.R.id.mtrl_picker_header_toggle)

                header?.setBackgroundColor(Color.WHITE)
                pickerContainer?.setBackgroundColor(Color.WHITE)

                toggleBtn?.setOnClickListener {
                    showCustomDatePickerInput(bindingBottomSheet)
                    picker.dismiss()
                }

            }
        })

        picker.addOnPositiveButtonClickListener { selection ->
            bindingBottomSheet.etPilihTgl.setText(handleDateRangeSelection(selection))
        }

        picker.addOnDismissListener {
            Locale.setDefault(oldLocale)
            val oldConfig = resources.configuration
            oldConfig.setLocale(oldLocale)
            @Suppress("DEPRECATION") resources.updateConfiguration(
                oldConfig, resources.displayMetrics
            )
        }

        picker.show(parentFragmentManager, "showCustomDatePicker")
    }

    @SuppressLint("SetTextI18n")
    private fun showCustomDatePickerInput(
        bindingBottomSheet: LayoutBottomSheetPeriodBinding,
    ) {
        if (parentFragmentManager.findFragmentByTag("showCustomDatePickerInput") != null) return

        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()

        val oldLocale = Locale.getDefault()
        Locale.setDefault(localeBuilder)

        val config = resources.configuration
        config.setLocale(localeBuilder)
        @Suppress("DEPRECATION") resources.updateConfiguration(config, resources.displayMetrics)

        val picker = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.MyCustomPicker)
            .setTitleText("Pilih tanggal").setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", localeBuilder))
            .setPositiveButtonText("Terapkan").setNegativeButtonText("Batal").build()

        picker.lifecycle.addObserver(object : DefaultLifecycleObserver {
            @SuppressLint("SetTextI18n")
            override fun onStart(owner: LifecycleOwner) {
                val header =
                    picker.view?.findViewById<View>(com.google.android.material.R.id.mtrl_picker_header)
                val headerText =
                    picker.view?.findViewById<TextView>(com.google.android.material.R.id.mtrl_picker_header_selection_text)
                val titleText =
                    picker.view?.findViewById<TextView>(com.google.android.material.R.id.mtrl_picker_title_text)
                val toggleBtn =
                    picker.view?.findViewById<ImageButton>(com.google.android.material.R.id.mtrl_picker_header_toggle)

                val confirmBtn =
                    picker.view?.findViewById<Button>(com.google.android.material.R.id.confirm_button)
                val cancelBtn =
                    picker.view?.findViewById<Button>(com.google.android.material.R.id.cancel_button)

                val mainPane =
                    picker.view?.findViewById<View>(com.google.android.material.R.id.mtrl_calendar_main_pane)

                header?.setBackgroundColor(Color.WHITE)
                headerText?.setTextColor(Color.BLACK)
                headerText?.text = "Masukkan Tgl"
                titleText?.setTextColor(Color.BLACK)
                titleText?.isAllCaps = false
                toggleBtn?.setColorFilter(Color.BLACK)

                confirmBtn?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.colorPrimary
                    )
                )
                confirmBtn?.isAllCaps = false
                cancelBtn?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.colorPrimary
                    )
                )
                cancelBtn?.isAllCaps = false
                mainPane?.setBackgroundColor(Color.WHITE)

                toggleBtn?.setOnClickListener {
                    showCustomDatePicker(bindingBottomSheet)
                    picker.dismiss()
                }

            }
        })

        picker.addOnPositiveButtonClickListener { selection ->
            bindingBottomSheet.etPilihTgl.setText(handleDateRangeSelection(selection))
        }

        picker.addOnDismissListener {
            Locale.setDefault(oldLocale)
            val oldConfig = resources.configuration
            oldConfig.setLocale(oldLocale)
            @Suppress("DEPRECATION") resources.updateConfiguration(
                oldConfig, resources.displayMetrics
            )
        }

        picker.show(parentFragmentManager, "showCustomDatePickerInput")
    }

    private fun navigateToDetail(financeIn: FinanceIn) {
        if (MyApp.isTablet(requireContext())) {
            val bundle = Bundle().apply {
                putParcelable("financeIn", financeIn)
            }
            val detailFragment = DetailUangFragment()
            detailFragment.arguments = bundle

            (activity as? FinanceActivity)?.findViewById<LinearLayout>(R.id.ll_menu)?.visibility =
                View.GONE

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.detail_container, detailFragment)
                .addToBackStack("detail")
                .commit()
        } else {
            val bundle = Bundle().apply {
                putParcelable("financeIn", financeIn)
            }
            findNavController().navigate(R.id.to_detailUangFragment, bundle)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleDateRangeSelection(selection: Pair<Long, Long>): String {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = selection.first
        startCalendar.set(Calendar.HOUR_OF_DAY, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        startCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)
        startDateTemporal = startCalendar.timeInMillis

        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = selection.second
        endCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endCalendar.set(Calendar.MINUTE, 59)
        endCalendar.set(Calendar.SECOND, 59)
        endCalendar.set(Calendar.MILLISECOND, 999)
        endDateTemporal = endCalendar.timeInMillis

        val localeBuilder = Locale.Builder().setLanguage("id").setRegion("ID").build()
        val sdf = SimpleDateFormat("dd MMM yyyy", localeBuilder)
        val startStr = sdf.format(Date(selection.first))
        val endStr = sdf.format(Date(selection.second))

        return "$startStr - $endStr"
    }
}

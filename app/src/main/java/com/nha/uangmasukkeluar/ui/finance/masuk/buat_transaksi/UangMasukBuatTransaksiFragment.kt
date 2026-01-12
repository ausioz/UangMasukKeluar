package com.nha.uangmasukkeluar.ui.finance.masuk.buat_transaksi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nha.uangmasukkeluar.R
import com.nha.uangmasukkeluar.ui.finance.modal.pelanggan.CariPelangganDialogFragment
import android.util.TypedValue
import androidx.activity.addCallback
import com.nha.uangmasukkeluar.databinding.FragmentUangMasukBuatTransaksiBinding
import com.nha.uangmasukkeluar.ui.finance.FinanceViewModel
import com.nha.uangmasukkeluar.ui.finance.modal.JenisPendapatanBottomSheetFragment
import com.nha.uangmasukkeluar.ui.finance.modal.UploadImageBottomSheetFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import com.nha.uangmasukkeluar.MyApp
import com.nha.uangmasukkeluar.domain.model.FinanceIn
import com.nha.uangmasukkeluar.ui.finance.FinanceActivity

class UangMasukBuatTransaksiFragment : Fragment() {

    private var _binding: FragmentUangMasukBuatTransaksiBinding? = null
    private val binding get() = _binding!!

    private val financeViewModel: FinanceViewModel by viewModel()

    private var imageUri: Uri? = null
    private var jenisPendapatan: String = JenisPendapatanBottomSheetFragment.JENIS_PENDAPATAN_LAIN
    private var cameraPhotoUri: Uri? = null

    private var isEditMode = false
    private var editFinanceInId: Int = 0
    private var originalDateTime: String = ""

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUangMasukBuatTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkEditModeAndSetTitle()
        setupOnBackPressed()
        setupActivityResultLaunchers()
        initUi()

        if (isEditMode) {
            populateDataForEdit()
        }
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.uangMasukMainFragment, false)
        }
    }

    private fun checkEditModeAndSetTitle() {
        @Suppress("DEPRECATION") val financeIn =
            arguments?.getParcelable<FinanceIn>("financeIn")

        if (financeIn != null) {
            isEditMode = true
            editFinanceInId = financeIn.id
            originalDateTime = financeIn.dateTime
            (activity as AppCompatActivity).supportActionBar?.title = "Edit Transaksi Uang Masuk"
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = "Buat Transaksi Uang Masuk"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateDataForEdit() {
        @Suppress("DEPRECATION") val financeIn =
            arguments?.getParcelable<FinanceIn>("financeIn") ?: return

        binding.etMasukKe.setText(financeIn.masukKe)
        binding.etTerimaDari.setText(financeIn.terimaDari)
        binding.etKeterangan.setText(financeIn.keterangan)
        binding.etJumlah.setText(financeIn.jumlah.toString())
        binding.etJenisPendapatan.setText(financeIn.jenisPendapatan)
        jenisPendapatan = financeIn.jenisPendapatan

        if (financeIn.buktiUri.isNotEmpty()) {
            imageUri = financeIn.buktiUri.toUri()
            displaySelectedImage(imageUri)
        }
    }

    private fun setupActivityResultLaunchers() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK && result.data?.data != null) {
                    imageUri = result.data?.data
                    takePersistentUriPermission(imageUri)
                    displaySelectedImage(imageUri)
                }
            }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri = cameraPhotoUri
                    displaySelectedImage(imageUri)
                }
            }

        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openCamera()
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        galleryPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openGallery()
                } else {
                    Toast.makeText(
                        requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initUi() {
        if (MyApp.isTablet(requireContext())) {
            (requireActivity() as FinanceActivity).showToolbar()
        }

        binding.etMasukKe.setText("Kasir Perangkat ke-49")

        binding.etJenisPendapatan.setOnClickListener {
            showJenisPendapatanBottomSheet()
        }

        binding.etTerimaDari.setOnClickListener {
            showCariPelangganDialog()
        }

        binding.etTerimaDari.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEndIndex = 2
                val drawable = binding.etTerimaDari.compoundDrawables[drawableEndIndex]
                if (drawable != null) {
                    val bounds = drawable.bounds
                    val hitPadding = 20.dpToPx(requireContext())
                    val drawableX = binding.etTerimaDari.width - bounds.width() - hitPadding
                    val drawableY = binding.etTerimaDari.height / 2 - bounds.height() / 2

                    if (event.rawX >= binding.etTerimaDari.left + drawableX && event.rawX <= binding.etTerimaDari.left + drawableX + bounds.width() + hitPadding * 2 && event.rawY >= binding.etTerimaDari.top + drawableY - hitPadding && event.rawY <= binding.etTerimaDari.top + drawableY + bounds.height() + hitPadding * 2) {
                        showCariPelangganDialog()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        binding.ivUpload.setOnClickListener {
            showUploadImageBottomSheet()
        }

        binding.ivUploadEdit.setOnClickListener {
            showUploadImageBottomSheet()
        }

        binding.ivUploadDelete.setOnClickListener {
            deleteImage()
        }

        binding.btSimpan.setOnClickListener {
            saveTransaction()
        }
    }

    private fun showJenisPendapatanBottomSheet() {
        val bottomSheet = JenisPendapatanBottomSheetFragment()

        bottomSheet.onPendapatanLainSelected = {
            jenisPendapatan = JenisPendapatanBottomSheetFragment.JENIS_PENDAPATAN_LAIN
            binding.etJenisPendapatan.setText(jenisPendapatan)
        }

        bottomSheet.onNonPendapatanSelected = {
            jenisPendapatan = JenisPendapatanBottomSheetFragment.JENIS_NON_PENDAPATAN
            binding.etJenisPendapatan.setText(jenisPendapatan)
        }

        bottomSheet.show(childFragmentManager, "jenisPendapatanBottomSheet")
    }

    private fun showUploadImageBottomSheet() {
        val bottomSheet = UploadImageBottomSheetFragment()

        bottomSheet.onGallerySelected = {
            checkGalleryPermissionAndOpen()
        }

        bottomSheet.onCameraSelected = {
            checkCameraPermissionAndOpen()
        }

        bottomSheet.show(childFragmentManager, "uploadImageBottomSheet")
    }

    private fun checkGalleryPermissionAndOpen() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            galleryPermissionLauncher.launch(permission)
        }
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        galleryLauncher.launch(intent)
    }

    private fun takePersistentUriPermission(uri: Uri?) {
        uri?.let {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                requireContext().contentResolver.takePersistableUriPermission(it, flags)
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraPhotoUri = createImageFileUri()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri)
        cameraLauncher.launch(intent)
    }

    private fun createImageFileUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "UANG_MASUK_$timeStamp"
        val storageDir = File(requireContext().getExternalFilesDir(null), "Pictures")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        return FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.fileprovider", imageFile
        )
    }

    private fun displaySelectedImage(uri: Uri?) {
        uri?.let {
            Glide.with(requireContext()).load(it).into(binding.ivUpload)
            binding.ivUploadEdit.visibility = View.VISIBLE
            binding.ivUploadDelete.visibility = View.VISIBLE
        }
    }

    private fun deleteImage() {
        imageUri = null
        binding.ivUpload.setImageResource(R.drawable.ic_upload_image)
        binding.ivUploadEdit.visibility = View.GONE
        binding.ivUploadDelete.visibility = View.GONE
    }

    private fun saveTransaction() {
        val terimaDari = binding.etTerimaDari.text.toString().trim()
        val keterangan = binding.etKeterangan.text.toString().trim()
        val jumlahStr = binding.etJumlah.text.toString().trim()

        if (terimaDari.isEmpty()) {
            binding.etTerimaDari.error = "Terima dari harus diisi"
            return
        }

        if (keterangan.isEmpty()) {
            binding.etKeterangan.error = "Keterangan harus diisi"
            return
        }

        if (jumlahStr.isEmpty()) {
            binding.etJumlah.error = "Jumlah harus diisi"
            return
        }

        val jumlah = try {
            jumlahStr.replace("Rp", "").replace(".", "").replace(",", "").trim().toInt()
        } catch (_: Exception) {
            binding.etJumlah.error = "Format jumlah tidak valid"
            return
        }

        val masukKe = binding.etMasukKe.text.toString().trim()
        val buktiUri = imageUri?.toString() ?: ""

        if (isEditMode) {
            financeViewModel.updateFinanceIn(
                id = editFinanceInId,
                dateTime = originalDateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
            Toast.makeText(requireContext(), "Transaksi berhasil diperbarui", Toast.LENGTH_SHORT)
                .show()
        } else {
            val dateTime =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            financeViewModel.addFinanceIn(
                dateTime = dateTime,
                masukKe = masukKe,
                terimaDari = terimaDari,
                keterangan = keterangan,
                jumlah = jumlah,
                jenisPendapatan = jenisPendapatan,
                buktiUri = buktiUri
            )
            Toast.makeText(requireContext(), "Transaksi berhasil disimpan", Toast.LENGTH_SHORT)
                .show()
        }

        findNavController().popBackStack(R.id.uangMasukMainFragment, false)
    }

    private fun showCariPelangganDialog() {
        val dialog = CariPelangganDialogFragment { pelanggan ->
            binding.etTerimaDari.setText(pelanggan.nama)
        }
        dialog.show(childFragmentManager, "cariPelangganDialog")
    }

    fun Int.dpToPx(context: android.content.Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
        ).toInt()
    }
}
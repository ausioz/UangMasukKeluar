package com.nha.uangmasukkeluar.ui.main

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nha.uangmasukkeluar.MyApp
import com.nha.uangmasukkeluar.R
import com.nha.uangmasukkeluar.databinding.ActivityMainBinding
import com.nha.uangmasukkeluar.databinding.LayoutBottomSheetMainMenuBinding
import com.nha.uangmasukkeluar.ui.base.BaseActivity
import com.nha.uangmasukkeluar.ui.finance.FinanceActivity

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btMenuFinance.setOnClickListener {
            if (MyApp.isTablet(this)) {
                val intent = Intent(this, FinanceActivity::class.java)
                intent.putExtra(START_DESTINATION, UANG_MASUK)
                startActivity(intent)
            } else {
                showBottomSheet()
            }
        }
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.bottomSheetDialogStyle)
        val bindingBottomSheet = LayoutBottomSheetMainMenuBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.root)

        bindingBottomSheet.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.clUangMasuk.setOnClickListener {
            val intent = Intent(this, FinanceActivity::class.java)
            intent.putExtra(START_DESTINATION, UANG_MASUK)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
        bindingBottomSheet.clUangKeluar.setOnClickListener {
            /*val intent = Intent(this, FinanceActivity::class.java)
            intent.putExtra(START_DESTINATION, UANG_KELUAR)
            startActivity(intent)
            bottomSheetDialog.dismiss()*/
        }
        bottomSheetDialog.show()
    }

    companion object {
        const val START_DESTINATION = "start_destination"
        const val UANG_MASUK = "uang_masuk"
        const val UANG_KELUAR = "uang_keluar"

    }

}
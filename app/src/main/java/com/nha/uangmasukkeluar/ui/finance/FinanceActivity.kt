package com.nha.uangmasukkeluar.ui.finance

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.nha.uangmasukkeluar.R
import com.nha.uangmasukkeluar.databinding.ActivityFinanceBinding
import com.nha.uangmasukkeluar.ui.base.BaseActivity
import com.nha.uangmasukkeluar.ui.main.MainActivity.Companion.START_DESTINATION
import com.nha.uangmasukkeluar.ui.main.MainActivity.Companion.UANG_MASUK

class FinanceActivity : BaseActivity() {

    private lateinit var binding: ActivityFinanceBinding
    private lateinit var navController: NavController
    private var startDestination: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startDestination = intent.getStringExtra(START_DESTINATION)

        setSupportActionBar(binding.toolBar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment

        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.finance_nav_graph)

        navController.graph = navGraph
        val appBarConfiguration = AppBarConfiguration(
            emptySet(),
            fallbackOnNavigateUpListener = {
                finish()
                true
            }
        )
        if (startDestination.isNullOrEmpty()) {
            finish()
        } else {
            when (startDestination) {
                UANG_MASUK -> {
                    navGraph.setStartDestination(R.id.uangMasukMainFragment)
//                    if (MyApp.isTablet(this)) {
//                        binding.tvUangMasuk?.setOnClickListener {}
//                        binding.tvUangkeluar?.setOnClickListener {}
//                    }
                }
//                UANG_KELUAR -> navGraph.setStartDestination(R.id.uangKeluarMainFragment)
            }
            binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun showLLMenu(){
        binding.llMenu?.visibility = View.VISIBLE
    }

    fun clearDetailContainer(){
        supportFragmentManager.beginTransaction()
            .remove(
                supportFragmentManager.findFragmentById(R.id.detail_container) ?: return
            )
            .commit()
    }

    fun hideToolbar(){
        binding.toolBar.visibility = View.GONE
    }

    fun showToolbar(){
        binding.toolBar.visibility = View.VISIBLE
    }

}
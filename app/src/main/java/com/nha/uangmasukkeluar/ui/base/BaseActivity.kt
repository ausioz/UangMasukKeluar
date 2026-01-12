package com.nha.uangmasukkeluar.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nha.uangmasukkeluar.MyApp

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApp.setOrientationByDevice(this)
    }
}
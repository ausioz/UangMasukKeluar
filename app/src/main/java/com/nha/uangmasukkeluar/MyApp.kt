package com.nha.uangmasukkeluar

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import com.nha.uangmasukkeluar.di.databaseModule
import com.nha.uangmasukkeluar.di.repositoryModule
import com.nha.uangmasukkeluar.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)

            modules(
                databaseModule,
                repositoryModule,
                viewModelModule
            )
        }
    }

    companion object {
        fun isTablet(context: Context): Boolean {
            val configuration: Configuration = context.resources.configuration
            return (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        fun setOrientationIfTablet(activity: Activity) {
            if (isTablet(activity)) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }
}

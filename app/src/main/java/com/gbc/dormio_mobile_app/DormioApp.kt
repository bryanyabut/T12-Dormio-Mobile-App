package com.gbc.dormio_mobile_app

import android.app.Application
import com.gbc.dormio_mobile_app.network.RetrofitClient

class DormioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}
package com.kest.softij

import android.app.Application

class SoftijApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        SoftijRepository.init(this)
    }
}
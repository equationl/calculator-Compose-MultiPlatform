package com.equationl.android

import android.app.Application
import com.equationl.common.utils.VibratorHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        VibratorHelper.instance.init(this)
    }
}
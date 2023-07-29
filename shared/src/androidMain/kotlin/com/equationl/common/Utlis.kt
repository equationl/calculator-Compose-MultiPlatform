package com.equationl.common

import android.app.Service
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 获取屏幕尺寸（减去虚拟按键的尺寸）
 * */
fun getScreenSize(context: Context): Point {
    val mWindowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    val mScreenWidth: Int
    val mScreenHeight: Int

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        mScreenHeight = mWindowManager.currentWindowMetrics.bounds.height()
        mScreenWidth = mWindowManager.currentWindowMetrics.bounds.width()
        return Point(mScreenWidth, mScreenHeight)
    }
    else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        val display = mWindowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(metrics)
        val point = Point()
        @Suppress("DEPRECATION")
        display.getRealSize(point)
        return point
    }
}
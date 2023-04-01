package com.equationl.common.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity

/**
 * FileName: VibratorHelper.kt
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/1 18:30
 * Description: Vibrator帮助类，用于解决旧版本兼容问题
 */
class VibratorHelper {
    private var vibrator: Vibrator? = null

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VibratorHelper()
        }
    }

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            this.vibrator = vibratorManager.defaultVibrator
        }
        else {
            @Suppress("DEPRECATION")
            this.vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun init(vibrator: Vibrator) {
        this.vibrator = vibrator
    }

    fun cancel() {
        vibrator?.cancel()
    }

    fun hasVibrator(): Boolean {
        return vibrator?.hasVibrator() == true
    }

    fun hasAmplitudeControl(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }
        return vibrator?.hasAmplitudeControl() == true
    }

    fun vibrate(timings: LongArray, amplitudes: IntArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat)
            vibrator?.vibrate(vibrationEffect)
        }
        else {
            val pattern = mutableListOf<Long>()
            var isCloseMotor = false
            var duration = 0L
            for (i in amplitudes.indices) {
                if ((amplitudes[i] > 0) == isCloseMotor) {
                    duration += timings[i]
                }
                else {
                    pattern.add(duration)
                    isCloseMotor = amplitudes[i] > 0
                    duration = timings[i]
                }
            }
            pattern.add(duration)

            val patternA = pattern.toLongArray()
            @Suppress("DEPRECATION")
            vibrator?.vibrate(patternA, repeat)
        }
    }

    fun vibrateOneShot(milliseconds: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator?.vibrate(vibrationEffect)
        }
        else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(milliseconds)
        }
    }

    fun vibratePredefined(predefined: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val vibrationEffect = VibrationEffect.createPredefined(predefined)
            vibrator?.vibrate(vibrationEffect)
        }
        else {
            // 系统预设效果就暂时不适配了
            throw UnsupportedOperationException("该系统不支持系统预设振动")
        }
    }

    /**
     * 计算错误时的振动效果
     * */
    fun vibrateOnError() {
        val timings = longArrayOf(10,180,10,90, 4,  90, 7, 80,2, 120,    4,50,2,40,1,40,     4,50,2,40,1,40,      4,50,2,40,1,40)
        val amplitudes = intArrayOf(255,0, 255,0, 240,0, 240,0, 240,0,    230,0,230,0,230,0,   220,0,220,0,220,0,   210,0,210,0,210,0)
        instance.vibrate(timings, amplitudes, -1)
    }

    /**
     * 清除时的振动效果
     * */
    fun vibrateOnClear() {
        val timings = longArrayOf(10,180,10,90, 4,  90, 7, 80,2, 120 )
        val amplitudes = intArrayOf(255,0, 255,0, 240,0, 240,0, 240,0)
        instance.vibrate(timings, amplitudes, -1)
    }

    /**
     * 开始计算时的振动效果
     * */
    fun vibrateOnEqual() {
        instance.vibrateOneShot(50, 150)
    }

    /**
     * 按下按键时的振动效果
     * */
    fun vibrateOnClick() {
        instance.vibrateOneShot(5, 255)
    }
}
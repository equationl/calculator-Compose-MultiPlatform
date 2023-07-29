package com.equationl.common.platform

import com.equationl.common.utils.VibratorHelper

actual fun vibrateOnClick() {
    VibratorHelper.instance.vibrateOnClick()
}

actual fun vibrateOnError() {
    VibratorHelper.instance.vibrateOnError()
}

actual fun vibrateOnClear() {
    VibratorHelper.instance.vibrateOnClear()
}

actual fun vibrateOnEqual() {
    VibratorHelper.instance.vibrateOnEqual()
}
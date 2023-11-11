package com.equationl.common.platform

/**
 * type:
 *
 * 0 - light vibrate
 *
 * 1 - success vibrate
 *
 * 2 - error vibrate
 *
 * 3 - warn vibrate
 *
 * */
var vibrateFunc: ((type: Int) -> Unit)? = null


actual fun vibrateOnClick() {
    vibrateFunc?.invoke(0)
}

actual fun vibrateOnError() {
    vibrateFunc?.invoke(2)
}

actual fun vibrateOnClear() {
    vibrateFunc?.invoke(3)
}

actual fun vibrateOnEqual() {
    vibrateFunc?.invoke(1)
}
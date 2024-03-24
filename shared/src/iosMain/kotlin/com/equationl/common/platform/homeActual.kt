package com.equationl.common.platform

import com.equationl.common.viewModel.KeyboardTypeStandard
import showSnack

var changeScreenOrientationFunc: ((to: Int) -> Unit)? = null


actual fun showFloatWindows() {
    showSnack("iOS暂不支持该功能")
}

actual fun changeKeyBoardType(changeTo: Int, isFromUser: Boolean) {
    if (changeTo == KeyboardTypeStandard) {
        changeScreenOrientationFunc?.invoke(0)
    }
    else {
        changeScreenOrientationFunc?.invoke(1)
    }
}

actual fun isNeedShowFloatBtn(): Boolean = false
package com.equationl.common.platform

import com.equationl.common.value.Config

actual fun showFloatWindows() {
    Config.isFloat.value = !Config.isFloat.value
}

actual fun changeKeyBoardType(changeTo: Int, isFromUser: Boolean) {
    Config.boardType.value = changeTo
}

actual fun isNeedShowFloatBtn(): Boolean = true

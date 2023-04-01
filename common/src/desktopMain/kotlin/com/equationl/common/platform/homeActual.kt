package com.equationl.common.platform

import com.equationl.common.value.Config

actual fun showFloatWindows() {
    Config.isFloat.value = !Config.isFloat.value
}

actual fun changeKeyBoardType(changeTo: Int) {
    Config.boardType.value = changeTo
}

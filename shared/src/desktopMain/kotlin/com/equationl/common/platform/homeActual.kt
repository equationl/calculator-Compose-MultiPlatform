package com.equationl.common.platform

import com.equationl.common.constant.PlatformType
import com.equationl.common.value.Config

actual suspend fun showFloatWindows() {
    Config.isFloat.value = !Config.isFloat.value
}

actual fun changeKeyBoardType(changeTo: Int, isFromUser: Boolean) {
    Config.boardType.value = changeTo
}

actual fun isNeedShowFloatBtn(): Boolean = true

actual fun currentPlatform(): PlatformType = PlatformType.Desktop

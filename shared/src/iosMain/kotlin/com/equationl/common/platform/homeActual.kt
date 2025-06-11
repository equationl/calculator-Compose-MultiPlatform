package com.equationl.common.platform

import com.equationl.common.constant.KeyBoardTypeEnum
import com.equationl.common.constant.PlatformType
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.tip_ios_not_support
import org.jetbrains.compose.resources.getString
import showSnack

/**
 * to: 0: 竖屏 1: 横屏
 * */
var changeScreenOrientationFunc: ((to: Int) -> Unit)? = null


actual suspend fun showFloatWindows() {
    showSnack(getString(Res.string.tip_ios_not_support))
}

actual fun changeKeyBoardType(changeTo: KeyBoardTypeEnum, isFromUser: Boolean) {
    if (changeTo == KeyBoardTypeEnum.Programmer) {
        changeScreenOrientationFunc?.invoke(1)
    }
    else {
        changeScreenOrientationFunc?.invoke(0)
    }
}

actual fun isNeedShowFloatBtn(): Boolean = false

actual fun currentPlatform(): PlatformType = PlatformType.Ios
package com.equationl.common.platform

import com.equationl.common.constant.PlatformType
import com.equationl.common.viewModel.KeyboardTypeStandard
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.tip_ios_not_support
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import showSnack

var changeScreenOrientationFunc: ((to: Int) -> Unit)? = null


@OptIn(ExperimentalResourceApi::class)
actual suspend fun showFloatWindows() {
    showSnack(getString(Res.string.tip_ios_not_support))
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

actual fun currentPlatform(): PlatformType = PlatformType.Ios
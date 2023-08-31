package com.equationl.common.platform

import com.equationl.common.viewModel.KeyboardTypeStandard
import showSnack

actual fun showFloatWindows() {
    showSnack("iOS暂不支持该功能")
}

actual fun changeKeyBoardType(changeTo: Int, isFromUser: Boolean) {
    // TODO 切换横竖屏
    if (changeTo == KeyboardTypeStandard) {
        showSnack("请手动旋转屏幕至竖屏")
    }
    else {
        showSnack("请手动旋转屏幕至横屏")
    }

/*    if (!isFromUser) return
    platform.vibrateOnClick()
    val activity = ActivityUtils.getTopActivity()
    activity?.requestedOrientation =
        if (changeTo == KeyboardTypeStandard)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE*/
}
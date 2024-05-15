package com.equationl.common.platform

import com.equationl.common.constant.PlatformType

expect suspend fun showFloatWindows()

expect fun changeKeyBoardType(changeTo: Int, isFromUser: Boolean)

expect fun isNeedShowFloatBtn(): Boolean

expect fun currentPlatform(): PlatformType
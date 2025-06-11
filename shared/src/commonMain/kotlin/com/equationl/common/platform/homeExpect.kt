package com.equationl.common.platform

import com.equationl.common.constant.KeyBoardTypeEnum
import com.equationl.common.constant.PlatformType

expect suspend fun showFloatWindows()

expect fun changeKeyBoardType(changeTo: KeyBoardTypeEnum, isFromUser: Boolean)

expect fun isNeedShowFloatBtn(): Boolean

expect fun currentPlatform(): PlatformType
package com.equationl.common.constant

/** 超过这个数值认为计算时间过长 */
const val CalculateTimeout = 100L

/** 长按超过该时间后触发连续键入 */
const val HoldPressStartTime = 1000L

/** 长按连续键入最小间隔时间 */
const val HoldPressMinInterval = 100L

enum class PlatformType {
    Desktop,
    Android,
    Ios,
}
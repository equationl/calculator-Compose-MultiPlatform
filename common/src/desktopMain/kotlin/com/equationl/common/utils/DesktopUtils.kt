package com.equationl.common.utils

import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.ui.input.key.KeyEvent
import com.equationl.common.dataModel.*


/**
 * 将 Ascii 转为 按键索引
 *
 * @return 按键索引，如果非法按键则返回 -1
 * */
fun asciiCode2BtnIndex(asciiCode: Int): Int {
    if (asciiCode in '0'.code..'9'.code)
        return asciiCode - 48
    if (asciiCode in 'A'.code..'F'.code)
        return asciiCode - 48
    if (asciiCode in 'a'.code..'f'.code)
        return asciiCode - 80

    return when(asciiCode) {
        '+'.code -> KeyIndex_Add
        '-'.code -> KeyIndex_Minus
        '*'.code -> KeyIndex_Multiply
        '/'.code -> KeyIndex_Divide
        '.'.code -> KeyIndex_Point
        '%'.code -> KeyIndex_Percentage
        '='.code, 10 -> KeyIndex_Equal // 10 回车
        8 -> KeyIndex_Back // 8 退格
        127 -> KeyIndex_Clear // 127 delete
        else -> -1
    }
}

/**
 * 判断是否是键入，避免重复写入
 *
 * 注意：回车和退格等按键不会触发 [KeyEvent.isTypedEvent] 所以需要从原始 KeyEvent 中截取结果
 * */
fun isKeyTyped(keyEvent: KeyEvent): Boolean {
    if (keyEvent.isTypedEvent) return true

    if (keyEvent.nativeKeyEvent.toString().contains("KEY_TYPED")) return true

    return false
}
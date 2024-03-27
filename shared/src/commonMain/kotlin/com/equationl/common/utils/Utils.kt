package com.equationl.common.utils

import com.equationl.common.constant.Text
import com.equationl.common.dataModel.InputBase
import com.equationl.common.viewModel.baseConversion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * 格式化显示的数字
 *
 * @param addSplitChar 添加的分隔符
 * @param splitLength 间隔多少个字符添加分割符
 * @param isAddLeadingZero 是否在不满足 [splitLength] 一组的数字前添加 0
 * @param formatDecimal 是否格式化小数部分（移除末尾多余的0）
 * @param formatInteger 是否格式化整数部分（添加分隔符或前导0）
 * */
fun String.formatNumber(
    addSplitChar: String = ",",
    splitLength: Int = 3,
    isAddLeadingZero: Boolean = false,
    formatDecimal: Boolean = false,
    formatInteger: Boolean = true
): String {
    // 如果是错误提示信息则不做处理
    if (this.length >= 3 && this.substring(0, 3) == "Err") return this

    val stringBuilder = StringBuilder(this)

    val pointIndex = stringBuilder.indexOf('.')

    val integer: StringBuilder
    val decimal: StringBuilder

    if (pointIndex == -1) {
        integer = stringBuilder // 整数部分
        decimal = StringBuilder() // 小数部分
    }
    else {
        val stringList = stringBuilder.split('.')
        integer = StringBuilder(stringList[0]) // 整数部分
        decimal = StringBuilder(stringList[1]) // 小数部分
        decimal.insert(0, '.')
    }

    var addCharCount = 0

    if (formatInteger) {
        // 给整数部分添加逗号分隔符
        if (integer.length > splitLength) {
            val end = if (integer[0] == '-') 2 else 1 // 判断是否有前导符号
            for (i in integer.length-splitLength downTo end step splitLength) {
                integer.insert(i, addSplitChar)
                addCharCount++
            }
        }

        if (isAddLeadingZero) { // 添加前导 0 补满一组
            val realLength = integer.length - addCharCount
            if (realLength % splitLength != 0) {
                repeat(4 - realLength % splitLength) {
                    integer.insert(0, '0')
                }
            }
        }
    }

    if (formatDecimal) {
        // 移除小数部分末尾占位的 0
        if (decimal.isNotEmpty()) {
            while (decimal.last() == '0') {
                decimal.deleteAt(decimal.lastIndex)
            }
            if (decimal.length == 1) { // 上面我们给小数部分首位添加了点号 ”.“ ，所以如果长度为 1 则表示不存在有效小数，则将点号也删除掉
                decimal.deleteAt(0)
            }
        }
    }

    return integer.append(decimal).toString()
}

/**
 * 添加指定前导 0 直至长度达到 [length]
 * */
fun String.addLeadingZero(length: Int = 64): String {
    var result = this

    if (result.length < length) {
        repeat(length - result.length) {
            result = "0$result"
        }
    }

    return result
}

/**
 * 移除所有前导 0
 *
 * @param isKeepAllZero 是否在字符全部为 0 时返回单个 0
 * */
fun String.removeLeadingZero(
    isKeepAllZero: Boolean = true
): String {
    if (this.isEmpty()) return this

    var index = 0
    for (c in this) {
        if (c != '0') break
        index++
    }

    if (isKeepAllZero && index == this.length) return "0"

    return this.substring(index)
}

/**
 * TODO 需要支持输入前导 0
 *
 * 将十六进制字符表示的整数代码转为 ASCII 字符
 *
 * NOTE：严格按照两位字符分割
 * */
fun String.formatHexToAscii(): String {
    if (this.isEmpty()) return this

    var result = ""
    var current = ""

    for (c in this) {
        current += c

        if (current.length < 2) {
            continue
        }

        val intNumber = current.baseConversion(InputBase.DEC, InputBase.HEX).toIntOrNull() ?: 0
        if (intNumber < 32) {
            result += getNonDisplayAscii(intNumber)
        }
        else {
            result += intNumber.toChar()
        }
        current = ""
    }

    return result
}

/**
 *
 * 将 ASCII 字符转为十六进制字符串
 *
 * NOTE：保证返回的十六进制字符串为两位一组
 *
 * */
fun String.formatAsciiToHex(): String {
    if (this.isEmpty()) return "0"

    var result = ""
    var currentText = this

    // 替换非打印字符
    Text.NonDisplayAscii.forEachIndexed { index, s ->
        currentText = currentText.replace(s, index.toChar().toString())
    }

    for (c in currentText) {
        if (c.code in 0..127) {
            var charText = c.code.toString().baseConversion(InputBase.HEX, InputBase.DEC)
            if (charText.length == 1) {
                charText = "0$charText"
            }
            result += charText
        }
    }

    return result.ifBlank { "0" }
}

fun getNonDisplayAscii(code: Int): String {
    return Text.NonDisplayAscii.getOrNull(code) ?: ""
}

suspend fun runWithTimeTip(
    timeOut: Long,
    runTask: suspend () -> Unit,
    onTimeout: suspend () -> Unit
) {
    supervisorScope {
        val tipJob = launch {
            delay(timeOut)
            onTimeout()
        }

        runTask()
        tipJob.cancel()
    }
}
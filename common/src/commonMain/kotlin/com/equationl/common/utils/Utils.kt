package com.equationl.common.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.equationl.common.dataModel.Operator
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * BigDecimal 的开平方
 *
 * @link https://stackoverflow.com/a/19743026
 * */
fun BigDecimal.sqrt(scale: Int): BigDecimal {
    val two = BigDecimal.valueOf(2)
    var x0 = BigDecimal("0")
    var x1 = BigDecimal(kotlin.math.sqrt(this.toDouble()))
    while (x0 != x1) {
        x0 = x1
        x1 = this.divide(x0, scale, BigDecimal.ROUND_HALF_UP)
        x1 = x1.add(x0)
        x1 = x1.divide(two, scale, BigDecimal.ROUND_HALF_UP)
    }
    return x1
}

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


fun calculate(leftValue: String, rightValue: String, operator: Operator, scale: Int = 16): Result<BigDecimal> {
    val left = BigDecimal(leftValue)
    val right = BigDecimal(rightValue)

    when (operator) {
        Operator.ADD -> {
            return Result.success(left.add(right))
        }
        Operator.MINUS -> {
            return Result.success(left.minus(right))
        }
        Operator.MULTIPLY -> {
            return  Result.success(left.multiply(right))
        }
        Operator.Divide -> {
            if (right.signum() == 0) {
                return Result.failure(ArithmeticException("Err: 除数不能为零"))
            }
            return  Result.success(left.divide(right, scale, RoundingMode.HALF_UP).stripTrailingZeros())
        }
        Operator.SQRT -> {
            if (left.signum() == -1) {
                return Result.failure(ArithmeticException("Err: 无效输入"))
            }
            return Result.success(left.sqrt(scale).stripTrailingZeros())
        }
        Operator.POW2 -> {
            val result = left.pow(2)
            if (result.toString().length > 5000) {
                return Result.failure(NumberFormatException("Err: 数字过大，无法显示"))
            }

            return Result.success(result)
        }
        Operator.NUll -> {
            return  Result.success(left)
        }
        Operator.NOT,
        Operator.AND,
        Operator.OR ,
        Operator.XOR,
        Operator.LSH,
        Operator.RSH -> {  // 这些值不会调用这个方法计算，所以直接返回错误
            return Result.failure(NumberFormatException("Err: 错误的调用"))
        }
    }
}

inline fun Modifier.noRippleClickable(crossinline onClick: ()->Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}
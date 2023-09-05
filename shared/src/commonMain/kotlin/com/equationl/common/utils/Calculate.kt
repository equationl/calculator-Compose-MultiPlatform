package com.equationl.common.utils

import com.equationl.common.dataModel.Operator
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal

/** 计算精度 */
const val DecimalPrecision = 64L

/**
 * BigDecimal 的开平方
 *
 * @param decimalPrecision 计算精度
 *
 * @link https://stackoverflow.com/a/19743026
 * */
fun BigDecimal.sqrt(decimalPrecision: Int = 16): BigDecimal {
    val two = BigDecimal.TWO
    var x0 = BigDecimal.ZERO
    var x1 = BigDecimal.fromDouble(kotlin.math.sqrt(this.doubleValue(false)))
    while (x0 != x1) {
        x0 = x1
        x1 = this.divide(x0, DecimalMode(decimalPrecision = decimalPrecision.toLong(), roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
        x1 = x1.add(x0)
        x1 = x1.divide(two, DecimalMode(decimalPrecision = decimalPrecision.toLong(), roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
    }
    return x1
}

fun calculate(leftValue: String, rightValue: String, operator: Operator): Result<BigDecimal> {
    val left = leftValue.toBigDecimal()
    val right = rightValue.toBigDecimal()

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
            return Result.success(left.divide(right, DecimalMode(roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, decimalPrecision = DecimalPrecision)))
        }
        Operator.SQRT -> {
            if (left.signum() == -1) {
                return Result.failure(ArithmeticException("Err: 无效输入"))
            }
            return Result.success(left.sqrt())
        }
        Operator.POW2 -> {
            val result = left.pow(2)
            //if (result.toString().length > 5000) {
            //return Result.failure(NumberFormatException("Err: 数字过大，无法显示"))
            //}

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
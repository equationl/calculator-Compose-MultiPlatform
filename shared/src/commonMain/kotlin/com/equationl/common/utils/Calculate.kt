package com.equationl.common.utils

import cancelSnack
import com.equationl.common.constant.CalculateTimeout
import com.equationl.common.dataModel.Operator
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.calculate_error_divide_by_zero
import com.equationl.shared.generated.resources.calculate_error_invalid_call
import com.equationl.shared.generated.resources.calculate_error_invalid_input
import com.equationl.shared.generated.resources.tip_calculating
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import showSnackSuspend

/** 计算精度 */
const val DecimalPrecision = 64L

val defaultDecimalModel = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, decimalPrecision = DecimalPrecision)

private var isCalculate = false

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

@OptIn(ExperimentalResourceApi::class)
suspend fun calculate(
    leftValue: String,
    rightValue: String,
    operator: Operator,
    decimalModel: DecimalMode = defaultDecimalModel
): Result<BigDecimal> {
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
                return Result.failure(ArithmeticException(getString(Res.string.calculate_error_divide_by_zero)))
            }
            return Result.success(left.divide(right, decimalModel))
        }
        Operator.SQRT -> {
            if (left.signum() == -1) {
                return Result.failure(ArithmeticException(getString(Res.string.calculate_error_invalid_input)))
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
            return Result.failure(NumberFormatException(getString(Res.string.calculate_error_invalid_call)))
        }
    }
}

suspend fun syncCalculate(
    leftValue: String,
    rightValue: String,
    operator: Operator,
    onFinish: (result: Result<BigDecimal>) -> Unit
) {
    syncCalculate(
        calculate = {
            calculate(leftValue, rightValue, operator)
        },
        onFinish = onFinish
    )
}

@OptIn(ExperimentalResourceApi::class)
suspend fun syncCalculate(
    calculate: suspend () -> Result<BigDecimal>,
    onFinish: (result: Result<BigDecimal>) -> Unit
) {
    // 避免重复
    if (isCalculate) return

    runWithTimeTip(
        timeOut = CalculateTimeout,
        runTask = {
            withContext(Dispatchers.Default) {
                isCalculate = true
                val result = calculate()
                cancelSnack()
                onFinish(result)
                isCalculate = false
            }
        },
        onTimeout = {
            showSnackSuspend(getString(Res.string.tip_calculating), true)
        }
    )
}
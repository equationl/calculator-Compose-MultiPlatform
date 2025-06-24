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

fun BigDecimal.ln(precision: Long = 30): BigDecimal {
    val decimalModel = DecimalMode(decimalPrecision = precision, roundingMode = RoundingMode.ROUND_HALF_TO_EVEN)

    // 处理特殊情况
    if (this <= BigDecimal.ZERO) {
        throw ArithmeticException("无法计算非正数的对数")
    }

    if (this == BigDecimal.ONE) {
        return BigDecimal.ZERO
    }

    // 使用变换 ln(x) = 2 * ln(sqrt(x)) 将 x 转换到接近 1 的范围
    var value = this
    var multiplier = BigDecimal.ONE

    // 如果 x 远大于 1，使用 ln(x) = ln(x/2^k) + k*ln(2)
    while (value > "2".toBigDecimal()) {
        value = value.divide(BigDecimal.TWO, decimalModel)
        multiplier = multiplier.add(BigDecimal.ONE)
    }

    // 如果 x 远小于 1，使用 ln(x) = ln(x*2^k) - k*ln(2)
    while (value < "0.4".toBigDecimal()) {
        value = value.multiply(BigDecimal.TWO)
        multiplier = multiplier.subtract(BigDecimal.ONE)
    }

    // 使用级数展开计算 ln(y) 其中 y 接近 1
    // ln(y) = 2 * (z + z^3/3 + z^5/5 + ...) 其中 z = (y-1)/(y+1)
    val y = value
    val z = (y.subtract(BigDecimal.ONE)).divide(y.add(BigDecimal.ONE), decimalModel)
    val z2 = z.multiply(z)

    var result = BigDecimal.ZERO
    var term = z
    var n = BigDecimal.ONE

    // 计算级数
    for (i in 0 until precision) {
        result = result.add(term.divide(n, decimalModel))
        n = n.add(BigDecimal.TWO)
        term = term.multiply(z2)

        // 当项变得足够小时停止
        if (term.abs() < BigDecimal.ONE.divide(BigDecimal.TEN.pow(precision), decimalModel)) {
            break
        }
    }

    result = result.multiply(BigDecimal.TWO)

    // 应用乘数调整
    if (multiplier != BigDecimal.ZERO) {
        val ln2 = "0.693147180559945309417232121458176568075500134360255254120680009".toBigDecimal()
        result = result.add(multiplier.multiply(ln2))
    }

    return result
}

fun BigDecimal.log10(precision: Long = 30): BigDecimal {
    val ln10 = "2.302585092994045684017991454684364207601101488628772976033327900".toBigDecimal()
    return this.ln(precision).divide(ln10, DecimalMode(precision, RoundingMode.ROUND_HALF_TO_EVEN))
}

fun BigDecimal.log(base: BigDecimal, precision: Long = 30): BigDecimal {
    if (base <= BigDecimal.ZERO || base == BigDecimal.ONE) {
        throw ArithmeticException("对数的底数必须为正数且不等于1")
    }
    return this.ln(precision).divide(base.ln(precision), DecimalMode(precision, RoundingMode.ROUND_HALF_TO_EVEN))
}

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
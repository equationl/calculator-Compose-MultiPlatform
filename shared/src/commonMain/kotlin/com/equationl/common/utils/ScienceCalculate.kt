package com.equationl.common.utils

import cancelSnack
import com.equationl.common.constant.CalculateTimeout
import com.equationl.common.dataModel.ScienceOperator
import com.equationl.common.dataModel.ScienceOperator.ADD
import com.equationl.common.dataModel.ScienceOperator.Abs
import com.equationl.common.dataModel.ScienceOperator.ArcCos
import com.equationl.common.dataModel.ScienceOperator.ArcCosH
import com.equationl.common.dataModel.ScienceOperator.ArcCot
import com.equationl.common.dataModel.ScienceOperator.ArcCotH
import com.equationl.common.dataModel.ScienceOperator.ArcCsc
import com.equationl.common.dataModel.ScienceOperator.ArcCscH
import com.equationl.common.dataModel.ScienceOperator.ArcSec
import com.equationl.common.dataModel.ScienceOperator.ArcSecH
import com.equationl.common.dataModel.ScienceOperator.ArcSin
import com.equationl.common.dataModel.ScienceOperator.ArcSinH
import com.equationl.common.dataModel.ScienceOperator.ArcTan
import com.equationl.common.dataModel.ScienceOperator.ArcTanH
import com.equationl.common.dataModel.ScienceOperator.Ceil
import com.equationl.common.dataModel.ScienceOperator.Cos
import com.equationl.common.dataModel.ScienceOperator.CosH
import com.equationl.common.dataModel.ScienceOperator.Cot
import com.equationl.common.dataModel.ScienceOperator.CotH
import com.equationl.common.dataModel.ScienceOperator.Csc
import com.equationl.common.dataModel.ScienceOperator.CscH
import com.equationl.common.dataModel.ScienceOperator.DEG
import com.equationl.common.dataModel.ScienceOperator.Divide
import com.equationl.common.dataModel.ScienceOperator.Dms
import com.equationl.common.dataModel.ScienceOperator.EPowX
import com.equationl.common.dataModel.ScienceOperator.Factorial
import com.equationl.common.dataModel.ScienceOperator.Floor
import com.equationl.common.dataModel.ScienceOperator.LeftBrackets
import com.equationl.common.dataModel.ScienceOperator.Ln
import com.equationl.common.dataModel.ScienceOperator.Log
import com.equationl.common.dataModel.ScienceOperator.LogYX
import com.equationl.common.dataModel.ScienceOperator.MINUS
import com.equationl.common.dataModel.ScienceOperator.MULTIPLY
import com.equationl.common.dataModel.ScienceOperator.Mod
import com.equationl.common.dataModel.ScienceOperator.NUll
import com.equationl.common.dataModel.ScienceOperator.Op10PowX
import com.equationl.common.dataModel.ScienceOperator.Op2PowX
import com.equationl.common.dataModel.ScienceOperator.Pow2
import com.equationl.common.dataModel.ScienceOperator.Pow3
import com.equationl.common.dataModel.ScienceOperator.Reciprocal
import com.equationl.common.dataModel.ScienceOperator.RightBrackets
import com.equationl.common.dataModel.ScienceOperator.Sec
import com.equationl.common.dataModel.ScienceOperator.SecH
import com.equationl.common.dataModel.ScienceOperator.Sin
import com.equationl.common.dataModel.ScienceOperator.SinH
import com.equationl.common.dataModel.ScienceOperator.Sqrt
import com.equationl.common.dataModel.ScienceOperator.Sqrt3
import com.equationl.common.dataModel.ScienceOperator.Tan
import com.equationl.common.dataModel.ScienceOperator.TanH
import com.equationl.common.dataModel.ScienceOperator.XPowY
import com.equationl.common.dataModel.ScienceOperator.XSqrtY
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.calculate_error_divide_by_zero
import com.equationl.shared.generated.resources.tip_calculating
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import showSnackSuspend
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh


object ScienceCalculate {

    private var isCalculate = false

    const val E = "2.7182818284590452353602874713527"
    const val PI = "3.1415926535897932384626433832795"

    private val E_BIG by lazy { E.toBigDecimal() }
    private val PI_BIG by lazy { PI.toBigDecimal() }
    
    // 角度模式
    private const val DEG_MODE = 0 // 角度制 0-360°
    private const val RAD_MODE = 1 // 弧度制 0-2π
    private const val GRAD_MODE = 2 // 百分度制 0-400
    
    // TODO 需要确定计算精度
    suspend fun calculate(
        leftValue: String,
        rightValue: String,
        operator: ScienceOperator,
        decimalModel: DecimalMode = defaultDecimalModel,
        angleType: Int = 0
    ): Result<BigDecimal> {
        val left = leftValue.toBigDecimal()
        val right = rightValue.toBigDecimal()

        when (operator) {
            ADD -> {
                return Result.success(left.add(right))
            }
            MINUS -> {
                return Result.success(left.minus(right))
            }
            MULTIPLY -> {
                return Result.success(left.multiply(right))
            }
            Divide -> {
                if (right.signum() == 0) {
                    return Result.failure(ArithmeticException(getString(Res.string.calculate_error_divide_by_zero)))
                }
                return Result.success(left.divide(right, decimalModel))
            }
            Pow2 -> {
                return Result.success(left.pow(2))
            }
            Pow3 -> {
                return Result.success(left.pow(3))
            }
            Abs -> {
                return Result.success(left.abs())
            }
            Mod -> {
                if (right.signum() == 0) {
                    return Result.failure(ArithmeticException(getString(Res.string.calculate_error_divide_by_zero)))
                }

                return Result.success(left.remainder(right))
            }
            Sqrt -> {
                if (left.signum() < 0) {
                    return Result.failure(ArithmeticException("无法计算负数的平方根"))
                }
                return Result.success(left.sqrt())
            }
            Sqrt3 -> {
                return Result.success(left.pow(BigDecimal.ONE.divide(3.toBigDecimal(), decimalModel).intValue()))
            }
            LeftBrackets -> {
                return Result.failure(NumberFormatException("TODO"))
            }
            RightBrackets -> {
                return Result.failure(NumberFormatException("TODO"))
            }
            Factorial -> {
                // 检查输入是否为非负整数
                if (left.signum() < 0 || left.remainder(BigDecimal.ONE).signum() != 0) {
                    return Result.failure(ArithmeticException("阶乘只对非负整数有定义"))
                }
                
                val n = left.intValue()
                
                // 0的阶乘为1
                if (n == 0) {
                    return Result.success(BigDecimal.ONE)
                }
                
                var result = BigDecimal.ONE
                for (i in 1..n) {
                    result = result.multiply(i.toBigDecimal())
                }
                
                return Result.success(result)
            }
            XPowY -> {
                return Result.success(left.pow(right.intValue()))
            }
            XSqrtY -> {
                if (left.signum() < 0 && right.remainder("2".toBigDecimal()).signum() == 0) {
                    return Result.failure(ArithmeticException("无法计算负数的偶次方根"))
                }
                val power = BigDecimal.ONE.divide(right, decimalModel).intValue()
                return Result.success(left.pow(power))
            }
            Op10PowX -> {
                return Result.success(BigDecimal.TEN.pow(left.intValue()))
            }
            Op2PowX -> {
                return Result.success(BigDecimal.TWO.pow(left.intValue()))
            }
            Log -> {
                if (left.signum() <= 0) {
                    return Result.failure(ArithmeticException("对数函数仅对正数有定义"))
                }
                return Result.success(left.log10())
            }
            LogYX -> {
                if (left.signum() <= 0 || right.signum() <= 0) {
                    return Result.failure(ArithmeticException("无法计算非正数的对数"))
                }
                return Result.success(right.log(left, decimalModel.decimalPrecision))
            }
            Ln -> {
                if (left.signum() <= 0) {
                    return Result.failure(ArithmeticException("无法计算非正数的自然对数"))
                }
                return Result.success(left.ln(decimalModel.decimalPrecision))
            }
            EPowX -> {
                return Result.success(E_BIG.pow(left.intValue()))
            }
            // FIXME 三角函数计算需要确定精度，如果转成 double 会丢精度
            Sin -> {
                val radValue = angleToRad(left, angleType)
                // 使用 Kotlin 标准库的 sin 函数，然后将结果转换为 BigDecimal
                val result = sin(radValue.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            Cos -> {
                val radValue = angleToRad(left, angleType)
                val result = cos(radValue.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            Tan -> {
                val radValue = angleToRad(left, angleType)
                // 检查是否在不可计算点上 (±90°, ±270°等处)
                val cosValue = cos(radValue.doubleValue(false))
                if (abs(cosValue) < 1e-10) {
                    return Result.failure(ArithmeticException("tan 函数在此点无定义"))
                }
                val result = tan(radValue.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            Sec -> {
                val radValue = angleToRad(left, angleType)
                val cosValue = cos(radValue.doubleValue(false))
                if (abs(cosValue) < 1e-10) {
                    return Result.failure(ArithmeticException("sec 函数在此点无定义"))
                }
                val result = 1.0 / cosValue
                return Result.success(result.toString().toBigDecimal())
            }
            Csc -> {
                val radValue = angleToRad(left, angleType)
                val sinValue = sin(radValue.doubleValue(false))
                if (abs(sinValue) < 1e-10) {
                    return Result.failure(ArithmeticException("csc 函数在此点无定义"))
                }
                val result = 1.0 / sinValue
                return Result.success(result.toString().toBigDecimal())
            }
            Cot -> {
                val radValue = angleToRad(left, angleType)
                val sinValue = sin(radValue.doubleValue(false))
                if (abs(sinValue) < 1e-10) {
                    return Result.failure(ArithmeticException("cot 函数在此点无定义"))
                }
                val result = cos(radValue.doubleValue(false)) / sinValue
                return Result.success(result.toString().toBigDecimal())
            }
            ArcSin -> {
                // 检查输入是否在 [-1, 1] 范围内
                if (left.doubleValue() < -1.0 || left.doubleValue() > 1.0) {
                    return Result.failure(ArithmeticException("arcsin 函数仅对 [-1, 1] 范围内的值有定义"))
                }
                val result = asin(left.doubleValue(false))
                return Result.success(radToAngle(result.toString().toBigDecimal(), angleType))
            }
            ArcCos -> {
                // 检查输入是否在 [-1, 1] 范围内
                if (left.doubleValue() < -1.0 || left.doubleValue() > 1.0) {
                    return Result.failure(ArithmeticException("arccos 函数仅对 [-1, 1] 范围内的值有定义"))
                }
                val result = acos(left.doubleValue(false))
                return Result.success(radToAngle(result.toString().toBigDecimal(), angleType))
            }
            ArcTan -> {
                val result = atan(left.doubleValue())
                return Result.success(radToAngle(result.toString().toBigDecimal(), angleType))
            }
            ArcSec -> {
                // sec(x) = 1/cos(x)，所以 arcSec(x) = arcCos(1/x)
                val value = left.doubleValue(false)
                if (abs(value) < 1.0) {
                    return Result.failure(ArithmeticException("arcsec 函数仅对 |x| ≥ 1 的值有定义"))
                }
                val result = acos(1.0 / value)
                return Result.success(radToAngle(result.toString().toBigDecimal(), angleType))
            }
            ArcCsc -> {
                // csc(x) = 1/sin(x)，所以 arcCsc(x) = arcSin(1/x)
                val value = left.doubleValue(false)
                if (abs(value) < 1.0) {
                    return Result.failure(ArithmeticException("arccsc 函数仅对 |x| ≥ 1 的值有定义"))
                }
                val result = asin(1.0 / value)
                return Result.success(radToAngle(result.toString().toBigDecimal(), angleType))
            }
            ArcCot -> {
                // cot(x) = cos(x)/sin(x) = 1/tan(x)，所以 arcCot(x) = arcTan(1/x)
                val value = left.doubleValue(false)
                val result = atan(1.0 / value)
                return Result.success(radToAngle(result.toString().toBigDecimal(), angleType))
            }
            SinH -> {
                val result = sinh(left.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            CosH -> {
                val result = cosh(left.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            TanH -> {
                val result = tanh(left.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            SecH -> {
                // sech(x) = 1/cosh(x)
                val result = 1.0 / cosh(left.doubleValue(false))
                return Result.success(result.toString().toBigDecimal())
            }
            CscH -> {
                // csch(x) = 1/sinh(x)
                val value = left.doubleValue(false)
                if (abs(value) < 1e-10) {
                    return Result.failure(ArithmeticException("csch 函数在 0 点无定义"))
                }
                val result = 1.0 / sinh(value)
                return Result.success(result.toString().toBigDecimal())
            }
            CotH -> {
                // coth(x) = cosh(x)/sinh(x)
                val value = left.doubleValue(false)
                if (abs(value) < 1e-10) {
                    return Result.failure(ArithmeticException("coth 函数在 0 点无定义"))
                }
                val result = cosh(value) / sinh(value)
                return Result.success(result.toString().toBigDecimal())
            }
            ArcSinH -> {
                // arcsinh(x) = ln(x + sqrt(x^2 + 1))
                val value = left.doubleValue(false)
                val result = ln(value + sqrt(value * value + 1.0))
                return Result.success(result.toString().toBigDecimal())
            }
            ArcCosH -> {
                // arccosh(x) = ln(x + sqrt(x^2 - 1))
                val value = left.doubleValue(false)
                if (value < 1.0) {
                    return Result.failure(ArithmeticException("arccosh 函数仅对 x ≥ 1 的值有定义"))
                }
                val result = ln(value + sqrt(value * value - 1.0))
                return Result.success(result.toString().toBigDecimal())
            }
            ArcTanH -> {
                // arctanh(x) = 0.5 * ln((1 + x)/(1 - x))
                val value = left.doubleValue(false)
                if (abs(value) >= 1.0) {
                    return Result.failure(ArithmeticException("arctanh 函数仅对 |x| < 1 的值有定义"))
                }
                val result = 0.5 * ln((1.0 + value) / (1.0 - value))
                return Result.success(result.toString().toBigDecimal())
            }
            ArcSecH -> {
                // arcsech(x) = ln(1/x + sqrt(1/x^2 - 1))
                val value = left.doubleValue(false)
                if (value <= 0.0 || value > 1.0) {
                    return Result.failure(ArithmeticException("arcsech 函数仅对 0 < x ≤ 1 的值有定义"))
                }
                val invValue = 1.0 / value
                val result = ln(invValue + sqrt(invValue * invValue - 1.0))
                return Result.success(result.toString().toBigDecimal())
            }
            ArcCscH -> {
                // arccsch(x) = ln(1/x + sqrt(1/x^2 + 1))
                val value = left.doubleValue(false)
                if (abs(value) < 1e-10) {
                    return Result.failure(ArithmeticException("arccsch 函数在 0 点无定义"))
                }
                val invValue = 1.0 / value
                val result = ln(invValue + sqrt(invValue * invValue + 1.0))
                return Result.success(result.toString().toBigDecimal())
            }
            ArcCotH -> {
                // arccoth(x) = 0.5 * ln((x + 1)/(x - 1))
                val value = left.doubleValue(false)
                if (value > -1.0 && value < 1.0) {
                    return Result.failure(ArithmeticException("arccoth 函数仅对 |x| > 1 的值有定义"))
                }
                val result = 0.5 * ln((value + 1.0) / (value - 1.0))
                return Result.success(result.toString().toBigDecimal())
            }
            Floor -> {
                return Result.success(left.floor())
            }
            Ceil -> {
                return Result.success(left.ceil())
            }
            Dms -> {
                // 将十进制度数转换为度分秒格式
                // 例如: 45.5° 转换为 45°30'0"
                val decimalPart = left.abs().remainder(BigDecimal.ONE)
                val degrees = left.intValue(false)
                
                // 计算分钟
                val minutes = decimalPart.multiply(60.toBigDecimal()).intValue()
                
                // 计算秒
                val secondsDecimal = decimalPart.multiply(60.toBigDecimal()).remainder(BigDecimal.ONE)
                val seconds = secondsDecimal.multiply(60.toBigDecimal()).intValue()
                
                // 构建结果: 格式为 d.ms，其中 d 是度，m 是分钟（0-59），s 是秒（0-59）
                val dmsValue = degrees.toBigDecimal() +
                              (minutes.toBigDecimal().divide(100.toBigDecimal(), decimalModel)) + 
                              (seconds.toBigDecimal().divide(10000.toBigDecimal(), decimalModel))
                
                return Result.success(dmsValue)
            }
            DEG -> {
                // 将度分秒格式转换为十进制度
                // 例如: 45.3015 (表示45°30'15") 转换为 45.5042°

                // 获取整数部分（度）
                val degrees = left.intValue(false)
                
                // 获取小数部分
                val fractionalPart = left.minus(degrees.toBigDecimal())
                
                // 计算分钟部分（小数的前两位）
                val minutes = (fractionalPart.multiply(100.toBigDecimal())).intValue(false)
                
                // 计算秒部分（小数的后两位）
                val seconds = (fractionalPart.multiply(10000.toBigDecimal()).minus(minutes.toBigDecimal().multiply(100.toBigDecimal()))).intValue(false)
                
                // 转换为十进制度：度 + 分/60 + 秒/3600
                val decimalDegrees = degrees.toBigDecimal().add(
                    minutes.toBigDecimal().divide(60.toBigDecimal(), decimalModel)
                ).add(
                    seconds.toBigDecimal().divide(3600.toBigDecimal(), decimalModel)
                )
                
                return Result.success(decimalDegrees)
            }
            Reciprocal -> {
                if (left.signum() == 0) {
                    return Result.failure(ArithmeticException(getString(Res.string.calculate_error_divide_by_zero)))
                }
                return Result.success(BigDecimal.ONE.divide(left, decimalModel))
            }
            NUll -> {
                return Result.success(left)
            }

        }
    }

    suspend fun syncCalculate(
        leftValue: String,
        rightValue: String,
        operator: ScienceOperator,
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

    // 将角度转换为弧度
    private fun angleToRad(value: BigDecimal, angleType: Int): BigDecimal {
        return when (angleType) {
            DEG_MODE -> value.multiply(PI_BIG).divide(180.toBigDecimal(), defaultDecimalModel)
            GRAD_MODE -> value.multiply(PI_BIG).divide(200.toBigDecimal(), defaultDecimalModel)
            else -> value // RAD 模式不需要转换
        }
    }

    // 将弧度转换为角度
    private fun radToAngle(value: BigDecimal, angleType: Int): BigDecimal {
        return when (angleType) {
            DEG_MODE -> value.multiply(180.toBigDecimal()).divide(PI_BIG, defaultDecimalModel)
            GRAD_MODE -> value.multiply(200.toBigDecimal()).divide(PI_BIG, defaultDecimalModel)
            else -> value // RAD 模式不需要转换
        }
    }
}
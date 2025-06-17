package com.equationl.common.utils

import cancelSnack
import com.equationl.common.constant.CalculateTimeout
import com.equationl.common.dataModel.ScienceOperator
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.tip_calculating
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import showSnackSuspend


object ScienceCalculate {

    private var isCalculate = false

    // TODO
    suspend fun calculate(
        leftValue: String,
        rightValue: String,
        operator: ScienceOperator,
        decimalModel: DecimalMode = defaultDecimalModel
    ): Result<BigDecimal> {
        return Result.failure(Exception("Not support"))
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
}
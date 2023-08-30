package com.equationl.common.viewModel

import androidx.compose.runtime.*
import com.equationl.common.dataModel.*
import com.equationl.common.platform.vibrateOnClear
import com.equationl.common.platform.vibrateOnClick
import com.equationl.common.platform.vibrateOnEqual
import com.equationl.common.platform.vibrateOnError
import com.equationl.common.utils.LongUtil
import com.equationl.common.utils.calculate
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.flow.Flow

@Composable
fun programmerPresenter(
    programmerActionFlow: Flow<ProgrammerAction>
): ProgrammerState {
    val programmerState = remember { mutableStateOf(ProgrammerState()) }

    LaunchedEffect(Unit) {
        programmerActionFlow.collect {action ->
            when (action) {
                is ProgrammerAction.ChangeInputBase -> changeInputBase(action.inputBase, programmerState)
                is ProgrammerAction.ClickBtn -> clickBtn(action.no, programmerState)
            }
        }
    }

    return programmerState.value
}

/**标记第一个值输入后，是否开始输入第二个值*/
private var isInputSecondValue: Boolean = false
/**标记是否已计算最终结果*/
private var isCalculated: Boolean = false
/**标记是否添加了非四则运算的“高级”运算符*/
private var isAdvancedCalculated: Boolean = false
/**标记是否处于错误状态*/
private var isErr: Boolean = false

private fun changeInputBase(inputBase: InputBase, viewStates: MutableState<ProgrammerState>) {
    vibrateOnClick()
    viewStates.value = when (inputBase) {
        InputBase.HEX -> {
            if (viewStates.value.lastInputValue.isNotEmpty()) {
                viewStates.value.copy(
                    inputBase = inputBase,
                    inputValue = viewStates.value.inputHexText,
                    lastInputValue = viewStates.value.lastInputValue.baseConversion(inputBase, viewStates.value.inputBase)
                )
            }
            else {
                viewStates.value.copy(inputBase = inputBase, inputValue = viewStates.value.inputHexText)
            }
        }
        InputBase.DEC -> {
            if (viewStates.value.lastInputValue.isNotEmpty()) {
                viewStates.value.copy(inputBase = inputBase,
                    inputValue = viewStates.value.inputDecText,
                    lastInputValue = viewStates.value.lastInputValue.baseConversion(inputBase, viewStates.value.inputBase)
                )
            }
            else {
                viewStates.value.copy(inputBase = inputBase, inputValue = viewStates.value.inputDecText)
            }
        }
        InputBase.OCT -> {
            if (viewStates.value.lastInputValue.isNotEmpty()) {
                viewStates.value.copy(inputBase = inputBase,
                    inputValue = viewStates.value.inputOctText,
                    lastInputValue = viewStates.value.lastInputValue.baseConversion(inputBase, viewStates.value.inputBase)
                )

            }
            else {
                viewStates.value.copy(inputBase = inputBase, inputValue = viewStates.value.inputOctText)
            }
        }
        InputBase.BIN -> {
            if (viewStates.value.lastInputValue.isNotEmpty()) {
                viewStates.value.copy(inputBase = inputBase,
                    inputValue = viewStates.value.inputBinText,
                    lastInputValue = viewStates.value.lastInputValue.baseConversion(inputBase, viewStates.value.inputBase)
                )
            }
            else {
                viewStates.value.copy(inputBase = inputBase, inputValue = viewStates.value.inputBinText)
            }
        }
    }
}

private fun clickBtn(no: Int, viewStates: MutableState<ProgrammerState>) {
    if (isErr) {
        viewStates.value = ProgrammerState(inputBase = viewStates.value.inputBase)
        isErr = false
        isAdvancedCalculated = false
        isCalculated = false
        isInputSecondValue = false
    }

    // 48 == '0'.code
    if (no in KeyIndex_0..KeyIndex_F) {
        vibrateOnClick()
        val newValue: String =
            if (viewStates.value.inputValue == "0") {
                if (viewStates.value.inputOperator != Operator.NUll) isInputSecondValue = true
                if (isAdvancedCalculated && viewStates.value.inputOperator == Operator.NUll) {  // 如果在输入高级运算符后直接输入数字，则重置状态
                    isAdvancedCalculated = false
                    isCalculated = false
                    isInputSecondValue = false
                    viewStates.value = ProgrammerState(inputBase = viewStates.value.inputBase)
                    no.toString()
                }

                (48 + no).toChar().toString()
            }
            else if (viewStates.value.inputOperator != Operator.NUll && !isInputSecondValue) {
                isCalculated = false
                isInputSecondValue = true
                (48+no).toChar().toString()
            }
            else if (isCalculated) {
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = ProgrammerState(inputBase = viewStates.value.inputBase)
                (48+no).toChar().toString()
            }
            else if (isAdvancedCalculated&& viewStates.value.inputOperator == Operator.NUll) { // 如果在输入高级运算符后直接输入数字，则重置状态
                isAdvancedCalculated = false
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = ProgrammerState(inputBase = viewStates.value.inputBase)
                no.toString()
            }
            else viewStates.value.inputValue + (48+no).toChar().toString()

        // 溢出判断
        try {
            newValue.toLong(viewStates.value.inputBase.number)
        } catch (e: NumberFormatException) {
            return
        }

        viewStates.value = viewStates.value.copy(
            inputValue = newValue,
            inputHexText = newValue.baseConversion(InputBase.HEX, viewStates.value.inputBase),
            inputDecText = newValue.baseConversion(InputBase.DEC, viewStates.value.inputBase),
            inputOctText = newValue.baseConversion(InputBase.OCT, viewStates.value.inputBase),
            inputBinText = newValue.baseConversion(InputBase.BIN, viewStates.value.inputBase),
            isFinalResult = false)
    }

    when (no) {
        KeyIndex_Add -> { // "+"
            clickArithmetic(Operator.ADD, viewStates)
        }
        KeyIndex_Minus -> { // "-"
            clickArithmetic(Operator.MINUS, viewStates)
        }
        KeyIndex_Multiply -> { // "×"
            clickArithmetic(Operator.MULTIPLY, viewStates)
        }
        KeyIndex_Divide -> { // "÷"
            clickArithmetic(Operator.Divide, viewStates)
        }
        KeyIndex_And -> {
            clickArithmetic(Operator.AND, viewStates)
        }
        KeyIndex_Or -> {
            clickArithmetic(Operator.OR, viewStates)
        }
        KeyIndex_XOr -> {
            clickArithmetic(Operator.XOR, viewStates)
        }
        KeyIndex_Lsh -> {
            clickArithmetic(Operator.LSH, viewStates)
        }
        KeyIndex_Rsh -> {
            clickArithmetic(Operator.RSH, viewStates)
        }
        KeyIndex_Not -> {
            vibrateOnClick()
            clickNot(viewStates)
        }
        KeyIndex_CE -> { // "CE"
            vibrateOnClear()
            if (isCalculated) {
                clickClear(viewStates)
            }
            else {
                clickCE(viewStates)
            }
        }
        KeyIndex_Clear -> {  // "C"
            vibrateOnClear()
            clickClear(viewStates)
        }
        KeyIndex_Back -> { // "←"
            vibrateOnClick()
            if (viewStates.value.inputValue != "0") {
                var newValue = viewStates.value.inputValue.substring(0, viewStates.value.inputValue.length - 1)
                if (newValue.isEmpty()) newValue = "0"
                viewStates.value = viewStates.value.copy(
                    inputValue = newValue,
                    inputHexText = newValue.baseConversion(InputBase.HEX, viewStates.value.inputBase),
                    inputDecText = newValue.baseConversion(InputBase.DEC, viewStates.value.inputBase),
                    inputOctText = newValue.baseConversion(InputBase.OCT, viewStates.value.inputBase),
                    inputBinText = newValue.baseConversion(InputBase.BIN, viewStates.value.inputBase),
                )
            }
        }
        KeyIndex_Equal -> { // "="
            clickEqual(viewStates)
        }
    }
}

private fun clickCE(viewStates: MutableState<ProgrammerState>) {
    viewStates.value = viewStates.value.copy(
        inputValue = "0",
        inputHexText = "0",
        inputDecText = "0",
        inputOctText = "0",
        inputBinText = "0",
    )
}

private fun clickClear(viewStates: MutableState<ProgrammerState>) {
    isInputSecondValue = false
    isCalculated = false
    isAdvancedCalculated = false
    isErr = false
    viewStates.value = ProgrammerState(inputBase = viewStates.value.inputBase)
}

private fun String.baseConversion(target: InputBase, current: InputBase): String {
    if (current == target) return this

    // 如果直接转会出现无法直接转成有符号 long 的问题，所以这里使用 BigInteger 来转
    // 见： https://stackoverflow.com/questions/47452924/kotlin-numberformatexception

    // FIXME 这里不应该在这里处理，应该在点击删除的地方处理
    val long = if (this == "-") {
        0
    }
    else {
        BigInteger.parseString(this, current.number).longValue(false)
    }

    if (target == InputBase.BIN) {
        return LongUtil.toBinaryString(long)
    }

    if (target == InputBase.HEX) {
        return LongUtil.toHexString(long).uppercase()
    }

    if (target == InputBase.OCT) {
        return LongUtil.toOctalString(long)
    }

    // 如果直接使用 toString 会造成直接添加 - 号表示负数，例如十进制的 -10 转为二进制会变成 -1010
    // 这里需要的是无符号的表示方式，即 -10 的二进制数应该用 1111111111111111111111111111111111111111111111111111111111110110 表示
    return long.toString(target.number).uppercase()

    //return this.toLong(current.number).toString(target.number).uppercase()
}

private fun clickNot(viewStates: MutableState<ProgrammerState>) {
    // 转换成十进制的 long 类型来计算， 然后转回当前进制
    val result = viewStates.value.inputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase).toLong() // 转至十进制 long
        .inv().toString()  // 计算
        .baseConversion(viewStates.value.inputBase, InputBase.DEC) // 转回当前进制

    val newState = viewStates.value.copy(
        inputValue = result,
        inputHexText = result.baseConversion(InputBase.HEX, viewStates.value.inputBase),
        inputDecText = result.baseConversion(InputBase.DEC, viewStates.value.inputBase),
        inputOctText = result.baseConversion(InputBase.OCT, viewStates.value.inputBase),
        inputBinText = result.baseConversion(InputBase.BIN, viewStates.value.inputBase),
    )

    if (isInputSecondValue) {
        viewStates.value = newState.copy(
            showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}${Operator.NOT.showText}(${viewStates.value.inputValue})",
            isFinalResult = false
        )
    }
    else {
        viewStates.value = newState.copy(
            inputOperator = Operator.NUll,
            lastInputValue = result,
            showText = "${Operator.NOT.showText}(${viewStates.value.inputValue})",
            isFinalResult = false
        )
        isInputSecondValue = true
    }

    isAdvancedCalculated = true
}

private fun clickArithmetic(operator: Operator, viewStates: MutableState<ProgrammerState>) {
    vibrateOnClick()
    var newState = viewStates.value.copy(
        inputOperator = operator,
        lastInputValue = viewStates.value.inputValue,
        isFinalResult = false
    )
    if (isCalculated) {
        isCalculated = false
        isInputSecondValue = false
    }

    if (isAdvancedCalculated) {
        isInputSecondValue = false

        if (viewStates.value.inputOperator == Operator.NUll) {  // 第一次添加操作符
            newState = newState.copy(
                showText = "${viewStates.value.showText}${operator.showText}"
            )
        }
        else { // 不是第一次添加操作符，则需要把计算结果置于左边，并去掉高级运算的符号
            isCalculated = false
            isInputSecondValue = false

            clickEqual(viewStates)

            newState = newState.copy(
                lastInputValue = viewStates.value.inputValue,
                showText = "${viewStates.value.inputValue}${operator.showText}",
                inputValue = viewStates.value.inputValue
            )
        }
    }
    else {
        if (viewStates.value.inputOperator == Operator.NUll) { // 第一次添加操作符
            newState = newState.copy(
                showText = "${viewStates.value.inputValue}${operator.showText}"
            )
        }
        else { // 不是第一次添加操作符，则应该把结果算出来后放到左边
            isCalculated = false
            isInputSecondValue = false

            clickEqual(viewStates)

            newState = newState.copy(
                lastInputValue = viewStates.value.inputValue,
                showText = "${viewStates.value.inputValue}${operator.showText}",
                inputValue = viewStates.value.inputValue
            )
        }
    }

    viewStates.value = newState
}


private fun clickEqual(viewStates: MutableState<ProgrammerState>) {
    if (viewStates.value.inputOperator == Operator.NUll) {
        vibrateOnEqual()
        viewStates.value = if (isAdvancedCalculated) {
            viewStates.value.copy(
                lastInputValue = viewStates.value.inputValue,
                showText = "${viewStates.value.showText}=",
                isFinalResult = true
            )
        } else {
            viewStates.value.copy(
                lastInputValue = viewStates.value.inputValue,
                showText = "${viewStates.value.inputValue}=",
                isFinalResult = true
            )
        }

        isCalculated = true
    }
    else {
        val result = programmerCalculate(viewStates)

        if (result.isSuccess) {
            vibrateOnEqual()
            val resultText : String = try {
               result.getOrNull().toString().baseConversion(viewStates.value.inputBase, InputBase.DEC)
            } catch (e: NumberFormatException) {
                viewStates.value = viewStates.value.copy(
                    inputValue = "Err: 溢出",
                    inputHexText = "溢出",
                    inputDecText = "溢出",
                    inputOctText = "溢出",
                    inputBinText = "溢出",
                    showText = "",
                    isFinalResult = true
                )
                isCalculated = false
                isErr = true
                return
            }
            val inputValue = if (viewStates.value.inputValue.substring(0, 1) == "-") "(${viewStates.value.inputValue})" else viewStates.value.inputValue
            if (isAdvancedCalculated) {
                val index = viewStates.value.showText.indexOf(viewStates.value.inputOperator.showText)
                viewStates.value = if (index != -1 && index == viewStates.value.showText.lastIndex) {
                    viewStates.value.copy(
                        inputValue = resultText,
                        inputHexText = resultText.baseConversion(InputBase.HEX, viewStates.value.inputBase),
                        inputDecText = resultText.baseConversion(InputBase.DEC, viewStates.value.inputBase),
                        inputOctText = resultText.baseConversion(InputBase.OCT, viewStates.value.inputBase),
                        inputBinText = resultText.baseConversion(InputBase.BIN, viewStates.value.inputBase),
                        showText = "${viewStates.value.showText}$inputValue=",
                        isFinalResult = true
                    )
                } else {
                    viewStates.value.copy(
                        inputValue = resultText,
                        inputHexText = resultText.baseConversion(InputBase.HEX, viewStates.value.inputBase),
                        inputDecText = resultText.baseConversion(InputBase.DEC, viewStates.value.inputBase),
                        inputOctText = resultText.baseConversion(InputBase.OCT, viewStates.value.inputBase),
                        inputBinText = resultText.baseConversion(InputBase.BIN, viewStates.value.inputBase),
                        showText = "${viewStates.value.showText}=",
                        isFinalResult = true
                    )
                }
            }
            else {
                viewStates.value = viewStates.value.copy(
                    inputValue = resultText,
                    inputHexText = resultText.baseConversion(InputBase.HEX, viewStates.value.inputBase),
                    inputDecText = resultText.baseConversion(InputBase.DEC, viewStates.value.inputBase),
                    inputOctText = resultText.baseConversion(InputBase.OCT, viewStates.value.inputBase),
                    inputBinText = resultText.baseConversion(InputBase.BIN, viewStates.value.inputBase),
                    showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}$inputValue=",
                    isFinalResult = true
                )
            }
            isCalculated = true
        }
        else {
            vibrateOnError()
            viewStates.value = viewStates.value.copy(
                inputValue = result.exceptionOrNull()?.message ?: "Err",
                inputHexText = "Err",
                inputDecText = "Err",
                inputOctText = "Err",
                inputBinText = "Err",
                showText = "",
                isFinalResult = true
            )
            isCalculated = false
            isErr = true
        }
    }

    isAdvancedCalculated = false
}

/**
 * 该方法会将输入字符转换成十进制数字计算，并返回计算完成后的十进制数字的字符串形式
 * */
private fun programmerCalculate(viewStates: MutableState<ProgrammerState>): Result<String> {
    val leftNumber = viewStates.value.lastInputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase)
    val rightNumber = viewStates.value.inputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase)

    if (viewStates.value.inputOperator in BitOperationList) {
        when (viewStates.value.inputOperator) {
            Operator.AND -> {
                return Result.success(
                    (leftNumber.toLong() and rightNumber.toLong()).toString()
                )
            }
            Operator.OR -> {
                return Result.success(
                    (leftNumber.toLong() or rightNumber.toLong()).toString()
                )
            }
            Operator.XOR -> {
                return Result.success(
                    (leftNumber.toLong() xor rightNumber.toLong()).toString()
                )
            }
            Operator.LSH -> {
                return try {
                    Result.success(
                        (leftNumber.toLong() shl rightNumber.toInt()).toString()
                    )
                } catch (e: NumberFormatException) {
                    Result.failure(NumberFormatException("Err: 结果未定义"))
                }
            }
            Operator.RSH -> {
                return try {
                    Result.success(
                        (leftNumber.toLong() shr rightNumber.toInt()).toString()
                    )
                } catch (e: NumberFormatException) {
                    Result.failure(NumberFormatException("Err: 结果未定义"))
                }
            }
            else -> {
                // 剩下的操作不应该由此处计算，所以直接返回错误
                return Result.failure(NumberFormatException("Err: 错误的调用2"))
            }
        }
    }
    else {
        calculate(
            leftNumber,
            rightNumber,
            viewStates.value.inputOperator
        ).fold({
            try {
                it.toPlainString().toLong()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                return Result.failure(NumberFormatException("Err: 结果溢出"))
            }
            return Result.success(it.toPlainString())
        }, {
            return Result.failure(it)
        })
    }
}

data class ProgrammerState(
    val showText: String = "",
    val inputOperator: Operator = Operator.NUll,
    val lastInputValue: String = "",
    val inputValue: String = "0",
    val inputHexText: String = "0",
    val inputDecText: String = "0",
    val inputOctText: String = "0",
    val inputBinText: String = "0",
    val inputBase: InputBase = InputBase.DEC,
    val isFinalResult: Boolean = false
)

sealed class ProgrammerAction {
    data class ChangeInputBase(val inputBase: InputBase): ProgrammerAction()
    data class ClickBtn(val no: Int): ProgrammerAction()
}
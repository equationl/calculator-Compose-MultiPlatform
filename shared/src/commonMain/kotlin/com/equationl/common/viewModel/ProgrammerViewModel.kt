package com.equationl.common.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.equationl.common.dataModel.BitOperationList
import com.equationl.common.dataModel.InputBase
import com.equationl.common.dataModel.KeyIndex_0
import com.equationl.common.dataModel.KeyIndex_Add
import com.equationl.common.dataModel.KeyIndex_And
import com.equationl.common.dataModel.KeyIndex_Back
import com.equationl.common.dataModel.KeyIndex_CE
import com.equationl.common.dataModel.KeyIndex_Clear
import com.equationl.common.dataModel.KeyIndex_Divide
import com.equationl.common.dataModel.KeyIndex_Equal
import com.equationl.common.dataModel.KeyIndex_F
import com.equationl.common.dataModel.KeyIndex_Lsh
import com.equationl.common.dataModel.KeyIndex_Minus
import com.equationl.common.dataModel.KeyIndex_Multiply
import com.equationl.common.dataModel.KeyIndex_Not
import com.equationl.common.dataModel.KeyIndex_Or
import com.equationl.common.dataModel.KeyIndex_Rsh
import com.equationl.common.dataModel.KeyIndex_XOr
import com.equationl.common.dataModel.Operator
import com.equationl.common.platform.vibrateOnClear
import com.equationl.common.platform.vibrateOnClick
import com.equationl.common.platform.vibrateOnEqual
import com.equationl.common.platform.vibrateOnError
import com.equationl.common.utils.addLeadingZero
import com.equationl.common.utils.calculate
import com.equationl.common.utils.defaultDecimalModel
import com.equationl.common.utils.formatAsciiToHex
import com.equationl.common.utils.removeLeadingZero
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import hideKeyBoard
import kotlinx.coroutines.flow.Flow
import showSnack

private val programmerState = mutableStateOf(ProgrammerState())

@Composable
fun programmerPresenter(
    programmerActionFlow: Flow<ProgrammerAction>
): ProgrammerState {
    val programmerState = remember { programmerState }

    LaunchedEffect(Unit) {
        programmerActionFlow.collect {action ->
            when (action) {
                is ProgrammerAction.ChangeInputBase -> changeInputBase(action.inputBase, programmerState)
                is ProgrammerAction.ClickBtn -> clickBtn(action.no, programmerState)
                is ProgrammerAction.ClickBitBtn -> clickBitBtn(action.no, programmerState)
                is ProgrammerAction.ClickChangeLength -> clickChangeLength(programmerState)
                is ProgrammerAction.ToggleShowAscii -> toggleShowAscii(programmerState)
                is ProgrammerAction.ChangeAsciiValue -> changeAsciiValue(action.text, programmerState)
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
/** 标记输入新的数字时是否需要清除当前输入值 */
private var isNeedClrInput: Boolean = false


private fun changeAsciiValue(text: String, viewStates: MutableState<ProgrammerState>) {
    val newValue = text.formatAsciiToHex()

    viewStates.value = viewStates.value.copy(
        inputValue = newValue.baseConversion(InputBase.HEX, InputBase.HEX),
        inputHexText = newValue.baseConversion(InputBase.HEX, InputBase.HEX),
        inputDecText = newValue.baseConversion(InputBase.DEC, InputBase.HEX),
        inputOctText = newValue.baseConversion(InputBase.OCT, InputBase.HEX),
        inputBinText = newValue.baseConversion(InputBase.BIN, InputBase.HEX),
        isFinalResult = false
    )
}

private fun toggleShowAscii(state: MutableState<ProgrammerState>) {
    hideKeyBoard()
    changeInputBase(InputBase.HEX, state)

    if (state.value.isShowAscii && lengthOverFlow(state, state.value.inputValue)) { // 切换回计算器模式，需要做溢出判断
        showSnack("数据溢出4564")
        // 当前数据溢出，清空数据
        state.value = state.value.copy(
            isShowAscii = !state.value.isShowAscii,
            inputValue = "0",
            inputHexText = "0",
            inputDecText = "0",
            inputOctText = "0",
            inputBinText = "0",
            isFinalResult = false
        )
        return
    }

    state.value = state.value.copy(
        isShowAscii = !state.value.isShowAscii,
    )
}

private fun clickChangeLength(state: MutableState<ProgrammerState>) {
    val newLength = when (state.value.currentLength) {
        ProgrammerLength.QWORD -> ProgrammerLength.DWORD
        ProgrammerLength.DWORD -> ProgrammerLength.WORD
        ProgrammerLength.WORD -> ProgrammerLength.BYTE
        ProgrammerLength.BYTE -> ProgrammerLength.QWORD
    }

    // TODO 这里应该是对数据做转换而不是直接清除数据
    state.value = ProgrammerState(
        currentLength = newLength,
        inputBase = state.value.inputBase,
        isShowAscii = state.value.isShowAscii,
    )
}

private fun clickBitBtn(no: Int, viewStates: MutableState<ProgrammerState>) {
    vibrateOnClick()

    hideKeyBoard()

    var binValue = viewStates.value.inputValue.baseConversion(InputBase.BIN, viewStates.value.inputBase).addLeadingZero()

    val charArray = binValue.toCharArray()
    charArray[no] = if (binValue[no] == '0') '1' else '0'

    binValue = charArray.concatToString().removeLeadingZero()

    viewStates.value = viewStates.value.copy(
        inputValue = binValue.baseConversion(viewStates.value.inputBase, InputBase.BIN),
        inputHexText = binValue.baseConversion(InputBase.HEX, InputBase.BIN),
        inputDecText = binValue.baseConversion(InputBase.DEC, InputBase.BIN),
        inputOctText = binValue.baseConversion(InputBase.OCT, InputBase.BIN),
        inputBinText = binValue.baseConversion(InputBase.BIN, InputBase.BIN),
        isFinalResult = false
    )
}

private fun changeInputBase(inputBase: InputBase, viewStates: MutableState<ProgrammerState>) {
    vibrateOnClick()
    hideKeyBoard()
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
    hideKeyBoard()

    if (isErr) {
        viewStates.value = ProgrammerState(
            inputBase = viewStates.value.inputBase,
            currentLength = viewStates.value.currentLength
        )
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
            else if (isAdvancedCalculated && viewStates.value.inputOperator == Operator.NUll) { // 如果在输入高级运算符后直接输入数字，则重置状态
                isAdvancedCalculated = false
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = ProgrammerState(inputBase = viewStates.value.inputBase)
                no.toString()
            }
            else if (!isCalculated && isInputSecondValue && isNeedClrInput) {
                isNeedClrInput = false
                no.toString()
            }
            else viewStates.value.inputValue + (48+no).toChar().toString()

        if (!viewStates.value.isShowAscii) {
            // 溢出判断
            if (lengthOverFlow(viewStates, newValue)) return
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
                if (newValue.isEmpty() || newValue == "-") newValue = "0"
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

private fun lengthOverFlow(
    viewStates: MutableState<ProgrammerState>,
    newValue: String
): Boolean {
    try {
        when (viewStates.value.currentLength) {
            // 如果是十进制使用有符号来判断
            // 否则使用无符号来判断
            ProgrammerLength.QWORD -> {
                if (viewStates.value.inputBase != InputBase.DEC) newValue.toULong(viewStates.value.inputBase.number)
                else newValue.toLong(viewStates.value.inputBase.number)
            }

            ProgrammerLength.DWORD -> {
                if (viewStates.value.inputBase != InputBase.DEC) newValue.toUInt(viewStates.value.inputBase.number)
                else newValue.toInt(viewStates.value.inputBase.number)
            }

            ProgrammerLength.WORD -> {
                if (viewStates.value.inputBase != InputBase.DEC) newValue.toUShort(viewStates.value.inputBase.number)
                else newValue.toUShort(viewStates.value.inputBase.number)
            }

            ProgrammerLength.BYTE -> {
                if (viewStates.value.inputBase != InputBase.DEC) newValue.toUByte(viewStates.value.inputBase.number)
                else newValue.toByte(viewStates.value.inputBase.number)
            }
        }
    } catch (e: NumberFormatException) {
        return true
    }
    return false
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
    viewStates.value = ProgrammerState(
        inputBase = viewStates.value.inputBase,
        currentLength = viewStates.value.currentLength,
        isShowAscii = viewStates.value.isShowAscii,
    )
}

/**
 *
 * 进制转换，十进制数会被转换为有符号数据，其他进制使用无符号数据
 *
 * */
fun String.baseConversion(target: InputBase, current: InputBase): String {
    if (current == target) return this

    if (target == InputBase.DEC) {
        return when (programmerState.value.currentLength) {
            ProgrammerLength.QWORD -> {
                // 如果直接转会出现无法直接转成有符号 long 的问题，所以这里使用 BigInteger 来转，以下同理
                val value = BigInteger.parseString(this, current.number).longValue(false)
                value.toString(target.number).uppercase()
            }

            ProgrammerLength.DWORD -> {
                val value = BigInteger.parseString(this, current.number).intValue(false)
                value.toString(target.number).uppercase()
            }

            ProgrammerLength.WORD -> {
                val value = BigInteger.parseString(this, current.number).shortValue(false)
                value.toString(target.number).uppercase()
            }

            ProgrammerLength.BYTE -> {
                val value = BigInteger.parseString(this, current.number).byteValue(false)
                value.toString(target.number).uppercase()
            }
        }
    }

    return when (programmerState.value.currentLength) {
        ProgrammerLength.QWORD -> {
            // 如果直接转会出现无法直接转成有符号 long 的问题，所以这里使用 BigInteger 来转
            val value = BigInteger.parseString(this, current.number).longValue(false)
            value.toULong().toString(target.number).uppercase()
        }
        ProgrammerLength.DWORD -> {
            val value = BigInteger.parseString(this, current.number).intValue(false)
            value.toUInt().toString(target.number).uppercase()
        }
        ProgrammerLength.WORD -> {
            val value = BigInteger.parseString(this, current.number).shortValue(false)
            value.toUShort().toString(target.number).uppercase()
        }
        ProgrammerLength.BYTE -> {
            val value = BigInteger.parseString(this, current.number).byteValue(false)
            value.toUByte().toString(target.number).uppercase()
        }
    }
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
        else { // 不是第一次添加操作符
            isCalculated = false
            isInputSecondValue = true
            isNeedClrInput = true

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

            // 运算结果溢出判断
            // TODO 这里不应该直接报错，应该溢出为符合长度的数值
            // 溢出判断
            try {
                when (viewStates.value.currentLength) {
                    // 如果是十进制使用有符号来判断
                    // 否则使用无符号来判断
                    ProgrammerLength.QWORD -> {
                        if (viewStates.value.inputBase != InputBase.DEC) result.getOrNull().toString().toULong()
                        else result.getOrNull().toString().toLong()
                    }
                    ProgrammerLength.DWORD -> {
                        if (viewStates.value.inputBase != InputBase.DEC) result.getOrNull().toString().toUInt()
                        else result.getOrNull().toString().toInt()
                    }
                    ProgrammerLength.WORD -> {
                        if (viewStates.value.inputBase != InputBase.DEC) result.getOrNull().toString().toUShort()
                        else result.getOrNull().toString().toShort()
                    }
                    ProgrammerLength.BYTE -> {
                        if (viewStates.value.inputBase != InputBase.DEC) result.getOrNull().toString().toUByte()
                        else result.getOrNull().toString().toByte()
                    }
                }
            } catch (e: NumberFormatException) {
                println(e.stackTraceToString())
                viewStates.value = viewStates.value.copy(
                    inputValue = "Err: 溢出",
                    inputHexText = "Err: 溢出",
                    inputDecText = "Err: 溢出",
                    inputOctText = "Err: 溢出",
                    inputBinText = "Err: 溢出",
                    showText = "",
                    isFinalResult = true
                )
                isCalculated = false
                isErr = true
                return
            }

            val resultText : String = result.getOrNull().toString().baseConversion(viewStates.value.inputBase, InputBase.DEC)
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
                if (isCalculated) {
                    viewStates.value = viewStates.value.copy(
                        inputValue = resultText,
                        inputHexText = resultText.baseConversion(InputBase.HEX, viewStates.value.inputBase),
                        inputDecText = resultText.baseConversion(InputBase.DEC, viewStates.value.inputBase),
                        inputOctText = resultText.baseConversion(InputBase.OCT, viewStates.value.inputBase),
                        inputBinText = resultText.baseConversion(InputBase.BIN, viewStates.value.inputBase),
                        showText = "$inputValue${viewStates.value.inputOperator.showText}${viewStates.value.lastInputValue}=",
                        isFinalResult = true,
                    )
                }
                else {
                    viewStates.value = viewStates.value.copy(
                        inputValue = resultText,
                        inputHexText = resultText.baseConversion(InputBase.HEX, viewStates.value.inputBase),
                        inputDecText = resultText.baseConversion(InputBase.DEC, viewStates.value.inputBase),
                        inputOctText = resultText.baseConversion(InputBase.OCT, viewStates.value.inputBase),
                        inputBinText = resultText.baseConversion(InputBase.BIN, viewStates.value.inputBase),
                        showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}$inputValue=",
                        isFinalResult = true,
                        lastInputValue = viewStates.value.inputValue
                    )
                }
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
    val leftNumber: String
    val rightNumber: String
    if (isCalculated) {
        leftNumber = viewStates.value.inputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase)
        rightNumber = viewStates.value.lastInputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase)
    }
    else {
        leftNumber = viewStates.value.lastInputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase)
        rightNumber = viewStates.value.inputValue.baseConversion(InputBase.DEC, viewStates.value.inputBase)
    }

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
            viewStates.value.inputOperator,
            decimalModel = defaultDecimalModel.copy(scale = 0L, roundingMode = RoundingMode.TOWARDS_ZERO)
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
    val isFinalResult: Boolean = false,
    val currentLength: ProgrammerLength = ProgrammerLength.QWORD,
    val isShowAscii: Boolean = false,
)

sealed class ProgrammerAction {
    data object ClickChangeLength: ProgrammerAction()
    data object ToggleShowAscii: ProgrammerAction()
    data class ChangeInputBase(val inputBase: InputBase): ProgrammerAction()
    data class ClickBtn(val no: Int): ProgrammerAction()
    data class ClickBitBtn(val no: Int): ProgrammerAction()
    data class ChangeAsciiValue(val text: String): ProgrammerAction()
}

enum class ProgrammerLength(val showText: String, val bitNum: Int) {
    QWORD("QWORD", 64),
    DWORD("DWORD", 32),
    WORD("WORD", 16),
    BYTE("BYTE", 8)
}
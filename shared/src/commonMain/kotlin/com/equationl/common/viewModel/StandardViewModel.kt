package com.equationl.common.viewModel

import androidx.compose.runtime.*
import com.equationl.common.dataModel.*
import com.equationl.common.database.DataBase
import com.equationl.common.platform.vibrateOnClear
import com.equationl.common.platform.vibrateOnClick
import com.equationl.common.platform.vibrateOnEqual
import com.equationl.common.platform.vibrateOnError
import com.equationl.common.utils.calculate
import com.equationl.common.utils.formatNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun standardPresenter(
    standardActionFlow: Flow<StandardAction>
): StandardState {
    val standardState = remember { mutableStateOf(StandardState()) }

    LaunchedEffect(Unit) {
        standardActionFlow.collect { action ->
            when (action) {
                is StandardAction.ClickBtn -> clickBtn(action.no, standardState)
                is StandardAction.ToggleHistory -> toggleHistory(action.forceClose, standardState)
                is StandardAction.ReadFromHistory -> readFromHistory(action.item, standardState)
                is StandardAction.DeleteHistory -> deleteHistory(action.item, standardState)
            }
        }
    }

    return standardState.value
}


/**标记第一个值输入后，是否开始输入第二个值*/
private var isInputSecondValue: Boolean = false
/**标记是否已计算最终结果*/
private var isCalculated: Boolean = false
/**标记是否添加了非四则运算的“高级”运算符*/
private var isAdvancedCalculated: Boolean = false
/**标记是否处于错误状态*/
private var isErr: Boolean = false

private val dataBase = DataBase.instance

private fun toggleHistory(forceClose: Boolean, viewStates: MutableState<StandardState>) {
    vibrateOnClick()

    if (viewStates.value.historyList.isNotEmpty() || forceClose) {
        viewStates.value = viewStates.value.copy(historyList = listOf())
    }
    else {
        viewStates.value = viewStates.value.copy(historyList = listOf(
            HistoryData(-1, showText = "加载中……", "null", "null", Operator.NUll, "请稍候")
        ))

        CoroutineScope(Dispatchers.Default).launch {
            var list = dataBase.getAll()
            if (list.isEmpty()) {
                list = listOf(
                    HistoryData(-1, showText = "", "null", "null", Operator.NUll, "没有历史记录")
                )
            }
            viewStates.value = viewStates.value.copy(historyList = list)
        }
    }
}

private fun readFromHistory(item: HistoryData, viewStates: MutableState<StandardState>) {
    if (item.id != -1) {
        vibrateOnEqual()
        viewStates.value = StandardState(
            inputValue = item.result,
            lastInputValue = item.lastInputText,
            inputOperator = item.operator,
            showText = item.showText,
            isFinalResult = true
        )
    }
}

private fun deleteHistory(item: HistoryData?, viewStates: MutableState<StandardState>) {
    CoroutineScope(Dispatchers.Default).launch {
        vibrateOnError()
        viewStates.value = if (item == null) {
            dataBase.delete(null)
            viewStates.value.copy(historyList = listOf())
        } else {
            vibrateOnClick()
            dataBase.delete(item)
            val newList = mutableListOf<HistoryData>()
            newList.addAll(viewStates.value.historyList)
            newList.remove(item)

            viewStates.value.copy(historyList = newList)
        }
    }
}

private fun clickBtn(no: Int, viewStates: MutableState<StandardState>) {
    if (isErr) {
        viewStates.value = StandardState()
        isErr = false
        isAdvancedCalculated = false
        isCalculated = false
        isInputSecondValue = false
    }

    if (no in KeyIndex_0..KeyIndex_9) {
        vibrateOnClick()
        val newValue =
            if (viewStates.value.inputValue == "0") {
                if (viewStates.value.inputOperator != Operator.NUll) isInputSecondValue = true
                if (isAdvancedCalculated && viewStates.value.inputOperator == Operator.NUll) {  // 如果在输入高级运算符后直接输入数字，则重置状态
                    isAdvancedCalculated = false
                    isCalculated = false
                    isInputSecondValue = false
                    viewStates.value = StandardState()
                    no.toString()
                }
                no.toString()
            }
            else if (viewStates.value.inputOperator != Operator.NUll && !isInputSecondValue) {
                isCalculated = false
                isInputSecondValue = true
                no.toString()
            }
            else if (isCalculated) {
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = StandardState(
                    lastShowText =
                        if (!isAdvancedCalculated)
                            viewStates.value.showText+viewStates.value.inputValue
                        else viewStates.value.lastShowText
                )
                no.toString()
            }
            else if (isAdvancedCalculated && viewStates.value.inputOperator == Operator.NUll) { // 如果在输入高级运算符后直接输入数字，则重置状态
                isAdvancedCalculated = false
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = StandardState()
                no.toString()
            }
            else viewStates.value.inputValue + no.toString()

        viewStates.value = viewStates.value.copy(inputValue = newValue, isFinalResult = false)
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
        KeyIndex_NegativeNumber -> { // "+/-"
            vibrateOnClick()
            if (viewStates.value.inputValue != "0") {
                val newValue: String =
                    if (viewStates.value.inputValue.substring(0, 1) == "-") viewStates.value.inputValue.substring(1, viewStates.value.inputValue.length)
                    else "-" + viewStates.value.inputValue
                viewStates.value = viewStates.value.copy(inputValue = newValue, isFinalResult = false)
            }
        }
        KeyIndex_Point -> { // "."
            vibrateOnClick()
            if (viewStates.value.inputValue.indexOf('.') == -1) {
                viewStates.value = viewStates.value.copy(inputValue = viewStates.value.inputValue + ".")
            }
        }
        KeyIndex_Reciprocal -> { // "1/x"
            vibrateOnClick()
            clickReciprocal(viewStates)
        }
        KeyIndex_Pow2 -> { // "x²"
            vibrateOnClick()
            clickPow2(viewStates)
        }
        KeyIndex_Sqrt -> { // "√x"
            vibrateOnClick()
            clickSqrt(viewStates)
        }
        KeyIndex_Percentage -> { // "%"
            if (isInputSecondValue && viewStates.value.lastInputValue != "" && viewStates.value.inputOperator != Operator.NUll) {
                vibrateOnClick()
                var result: String = calculate(viewStates.value.inputValue, "100", Operator.Divide).getOrNull().toString()
                result = calculate(viewStates.value.lastInputValue, result, Operator.MULTIPLY).getOrNull().toString()

                viewStates.value = viewStates.value.copy(
                    inputValue = result,
                    showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}" +
                            result.formatNumber(formatDecimal = true, formatInteger = false),
                    isFinalResult = true
                )
            }
            else {
                vibrateOnClear()
                viewStates.value = viewStates.value.copy(
                    inputValue = "0",
                    showText = "0",
                    lastInputValue = "",
                    inputOperator = Operator.NUll
                )
            }
        }
        KeyIndex_Equal -> { // "="
            clickEqual(viewStates)
        }
        KeyIndex_CE -> { // "CE"
            vibrateOnClear()
            if (isCalculated) {
                clickClear(viewStates)
            }
            else {
                viewStates.value = viewStates.value.copy(inputValue = "0")
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
                viewStates.value = viewStates.value.copy(inputValue = newValue)
            }
        }
    }
}

private fun clickClear(viewStates: MutableState<StandardState>) {
    isInputSecondValue = false
    isCalculated = false
    isAdvancedCalculated = false
    isErr = false
    viewStates.value = StandardState()
}

private fun clickReciprocal(viewStates: MutableState<StandardState>) {
    val result = calculate("1", viewStates.value.inputValue, Operator.Divide)
    val resultText = if (result.isSuccess) {
        result.getOrNull()?.toPlainString() ?: "Null"
    } else {
        vibrateOnError()
        isErr = true
        result.exceptionOrNull()?.message ?: "Err"
    }

    val newState = viewStates.value.copy(
        inputValue = resultText
    )

    if (isInputSecondValue) {
        viewStates.value = newState.copy(
            showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}1/(${viewStates.value.inputValue})",
            isFinalResult = false,
            lastShowText =
            if (viewStates.value.showText.indexOf("=") != -1)
                viewStates.value.showText+viewStates.value.inputValue
            else viewStates.value.lastShowText
        )
    }
    else {
        viewStates.value = newState.copy(
            inputOperator = Operator.NUll,
            lastInputValue = viewStates.value.inputValue,
            showText = "1/(${viewStates.value.inputValue})",
            isFinalResult = false
        )
       // isInputSecondValue = true
    }

    isAdvancedCalculated = true
}

private fun clickSqrt(viewStates: MutableState<StandardState>) {
    val result = calculate(viewStates.value.inputValue, "0", Operator.SQRT)

    val resultText = if (result.isSuccess) {
        result.getOrNull()?.toPlainString() ?: "Null"
    } else {
        vibrateOnError()
        isErr = true
        result.exceptionOrNull()?.message ?: "Err"
    }

    val newState = viewStates.value.copy(
        inputValue = resultText
    )

    if (isInputSecondValue) {
        viewStates.value = newState.copy(
            showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}${Operator.SQRT.showText}(${viewStates.value.inputValue})",
            isFinalResult = false,
            lastShowText =
            if (viewStates.value.showText.indexOf("=") != -1)
                viewStates.value.showText+viewStates.value.inputValue
            else viewStates.value.lastShowText
        )
    }
    else {
        viewStates.value = newState.copy(
            inputOperator = Operator.NUll,
            lastInputValue = resultText,
            showText = "${Operator.SQRT.showText}(${viewStates.value.inputValue})",
            isFinalResult = false
        )
        //isInputSecondValue = true
    }

    isAdvancedCalculated = true
}

private fun clickPow2(viewStates: MutableState<StandardState>) {
    val result = calculate(viewStates.value.inputValue, "0", Operator.POW2)

    val resultText = if (result.isSuccess) {
        result.getOrNull()!!.toPlainString()
    } else {
        vibrateOnError()
        isErr = true
        result.exceptionOrNull()?.message ?: "Err"
    }

    val newState = viewStates.value.copy(
        inputValue = resultText
    )

    if (isInputSecondValue) {
        viewStates.value = newState.copy(
            showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}(${viewStates.value.inputValue})${Operator.POW2.showText}",
            isFinalResult = false,
            lastShowText =
                if (viewStates.value.showText.indexOf("=") != -1)
                    viewStates.value.showText+viewStates.value.inputValue
                else viewStates.value.lastShowText
        )
    }
    else {
        viewStates.value = newState.copy(
            inputOperator = Operator.NUll,
            lastInputValue = result.getOrNull().toString(),
            showText = "(${viewStates.value.inputValue})${Operator.POW2.showText}",
            isFinalResult = false
        )
        //isInputSecondValue = true
    }

    isAdvancedCalculated = true
}

private fun clickEqual(viewStates: MutableState<StandardState>) {
    val inputValueCache = viewStates.value.inputValue

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
        val result = calculate(viewStates.value.lastInputValue, viewStates.value.inputValue, viewStates.value.inputOperator)
        if (result.isSuccess) {
            vibrateOnEqual()
            val resultText = result.getOrNull()?.toPlainString() ?: "Null"
            val inputValue = if (viewStates.value.inputValue.substring(0, 1) == "-") "(${viewStates.value.inputValue})" else viewStates.value.inputValue
            if (isAdvancedCalculated) {
                val index = viewStates.value.showText.indexOf(viewStates.value.inputOperator.showText)
                viewStates.value = if (index != -1 && index == viewStates.value.showText.lastIndex) {
                    viewStates.value.copy(
                        inputValue = resultText,
                        showText = "${viewStates.value.showText}$inputValue=",
                        isFinalResult = true
                    )
                } else {
                    viewStates.value.copy(
                        inputValue = resultText,
                        showText = "${viewStates.value.showText}=",
                        isFinalResult = true
                    )
                }
            }
            else {
                viewStates.value = viewStates.value.copy(
                    inputValue = resultText,
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
                showText = "",
                isFinalResult = true
            )
            isCalculated = false
            isErr = true
        }
    }

    isAdvancedCalculated = false

    CoroutineScope(Dispatchers.Default).launch {
        withContext(Dispatchers.Default) {
            if (!isErr) {  // 不保存错误结果
                dataBase.insert(
                    HistoryData(
                        showText = viewStates.value.showText,
                        lastInputText = viewStates.value.lastInputValue,
                        operator = viewStates.value.inputOperator,
                        result = viewStates.value.inputValue,
                        inputText = inputValueCache
                    )
                )
            }
        }
    }
}

private fun clickArithmetic(operator: Operator, viewStates: MutableState<StandardState>) {
    vibrateOnClick()
    var newState = viewStates.value.copy(
        inputOperator = operator,
        lastInputValue = viewStates.value.inputValue,
        isFinalResult = false
    )
    if (isCalculated) {
        isCalculated = false
        isInputSecondValue = false
        newState = newState.copy(
            lastShowText =
            if (!isAdvancedCalculated)
                viewStates.value.showText+viewStates.value.inputValue
            else viewStates.value.lastShowText
        )
    }

    if (isAdvancedCalculated) {
        isInputSecondValue = false

        if (viewStates.value.inputOperator == Operator.NUll) {  // 第一次添加操作符
            newState = newState.copy(
                showText = "${viewStates.value.showText}${operator.showText}"
            )
        }
        else {  // 不是第一次添加操作符，则需要把计算结果置于左边，并去掉高级运算的符号
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

data class StandardState(
    val inputValue: String = "0",
    val inputOperator: Operator = Operator.NUll,
    val lastInputValue: String = "",
    val showText: String = "",
    val isFinalResult: Boolean = false,
    val historyList: List<HistoryData> = listOf(),
    val lastShowText: String = ""
)

sealed class StandardAction {
    data class ToggleHistory(val forceClose: Boolean = false): StandardAction()
    data class ClickBtn(val no: Int): StandardAction()
    data class ReadFromHistory(val item: HistoryData): StandardAction()
    data class DeleteHistory(val item: HistoryData?): StandardAction()
}
package com.equationl.common.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.equationl.common.constant.HoldPressMinInterval
import com.equationl.common.constant.HoldPressStartTime
import com.equationl.common.dataModel.HistoryData
import com.equationl.common.dataModel.KeyIndex_0
import com.equationl.common.dataModel.KeyIndex_9
import com.equationl.common.dataModel.KeyIndex_Add
import com.equationl.common.dataModel.KeyIndex_Back
import com.equationl.common.dataModel.KeyIndex_CE
import com.equationl.common.dataModel.KeyIndex_Clear
import com.equationl.common.dataModel.KeyIndex_Divide
import com.equationl.common.dataModel.KeyIndex_Equal
import com.equationl.common.dataModel.KeyIndex_MemoryClear
import com.equationl.common.dataModel.KeyIndex_MemoryList
import com.equationl.common.dataModel.KeyIndex_MemoryMinus
import com.equationl.common.dataModel.KeyIndex_MemoryPlus
import com.equationl.common.dataModel.KeyIndex_MemoryRead
import com.equationl.common.dataModel.KeyIndex_MemorySave
import com.equationl.common.dataModel.KeyIndex_Minus
import com.equationl.common.dataModel.KeyIndex_Multiply
import com.equationl.common.dataModel.KeyIndex_NegativeNumber
import com.equationl.common.dataModel.KeyIndex_Percentage
import com.equationl.common.dataModel.KeyIndex_Point
import com.equationl.common.dataModel.KeyIndex_Pow2
import com.equationl.common.dataModel.KeyIndex_Reciprocal
import com.equationl.common.dataModel.KeyIndex_Sqrt
import com.equationl.common.dataModel.MemoryData
import com.equationl.common.dataModel.Operator
import com.equationl.common.database.HistoryDb
import com.equationl.common.platform.vibrateOnClear
import com.equationl.common.platform.vibrateOnClick
import com.equationl.common.platform.vibrateOnEqual
import com.equationl.common.platform.vibrateOnError
import com.equationl.common.utils.calculate
import com.equationl.common.utils.formatNumber
import com.equationl.common.utils.syncCalculate
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.history_is_empty
import com.equationl.shared.generated.resources.loading
import com.equationl.shared.generated.resources.please_wait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

private var holdPressJob: Job? = null

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
                is StandardAction.ToggleMemoryScreen -> toggleMemoryList(action.forceClose, standardState)
                is StandardAction.ReadFromHistory -> readFromHistory(action.item, standardState)
                is StandardAction.DeleteHistory -> deleteHistory(action.item, standardState)
                is StandardAction.DeleteMemoryItem -> deleteMemoryItem(action.item, standardState)
                is StandardAction.Init -> init(action.coroutineScope, standardState)
                is StandardAction.OnHoldPress -> {
                    holdPressJob?.cancel()
                    holdPressJob = launch {
                        onHoldPress(action.isPress, action.no, standardState)
                    }
                }
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
/** 标记输入新的数字时是否需要清除当前输入值 */
private var isNeedClrInput: Boolean = false

private val historyDao = HistoryDb.instance.history()

private fun init(coroutineScope: CoroutineScope, viewStates: MutableState<StandardState>) {
    CoroutineScope(Dispatchers.Default).launch {
        val memoryData = historyDao.getAllMemory()
        viewStates.value = viewStates.value.copy(coroutineScope = coroutineScope, memoryData = memoryData)
    }
}

@OptIn(ExperimentalResourceApi::class)
private suspend fun toggleHistory(forceClose: Boolean, viewStates: MutableState<StandardState>) {
    vibrateOnClick()

    if (viewStates.value.historyList.isNotEmpty() || forceClose) {
        viewStates.value = viewStates.value.copy(historyList = listOf())
    }
    else {
        viewStates.value = viewStates.value.copy(historyList = listOf(
            HistoryData(-1, showText = getString(Res.string.loading), "null", "null", Operator.NUll, getString(Res.string.please_wait))
        ))

        CoroutineScope(Dispatchers.Default).launch {
            var list = historyDao.getAll()
            if (list.isEmpty()) {
                list = listOf(
                    HistoryData(-1, showText = "", "null", "null", Operator.NUll, getString(Res.string.history_is_empty))
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
            historyDao.deleteAll()
            viewStates.value.copy(historyList = listOf())
        } else {
            vibrateOnClick()
            historyDao.delete(item)
            val newList = mutableListOf<HistoryData>()
            newList.addAll(viewStates.value.historyList)
            newList.remove(item)

            viewStates.value.copy(historyList = newList)
        }
    }
}

private fun deleteMemoryItem(item: MemoryData, viewStates: MutableState<StandardState>) {
    CoroutineScope(Dispatchers.Default).launch {
        historyDao.deleteMemory(item)
        val newList = viewStates.value.memoryData - item
        viewStates.value = viewStates.value.copy(memoryData = newList, isShowMemoryScreen = newList.isNotEmpty())
    }
}

private suspend fun onHoldPress(isPress: Boolean, no: Int, viewStates: MutableState<StandardState>) {
    if (isPress) {
        // 先触发一次点击事件
        clickBtn(no, viewStates)

        withContext(Dispatchers.IO) {
            var interval = HoldPressStartTime
            while (true) {
                delay(interval.coerceAtLeast(HoldPressMinInterval))
                if (interval > HoldPressMinInterval) {
                    interval -= 150L
                }

                clickBtn(no, viewStates)
            }
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
            else if (!isCalculated && isInputSecondValue && isNeedClrInput) {
                isNeedClrInput = false
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

                viewStates.value.coroutineScope?.launch {
                    syncCalculate(
                        calculate = {
                            val temp: String = calculate(viewStates.value.inputValue, "100", Operator.Divide).getOrNull().toString()
                            calculate(viewStates.value.lastInputValue, temp, Operator.MULTIPLY)
                        },
                        onFinish = { resultBigDecimal ->
                            val result = resultBigDecimal.getOrNull().toString()
                            viewStates.value = viewStates.value.copy(
                                inputValue = result,
                                showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}" +
                                        result.formatNumber(formatDecimal = true, formatInteger = false),
                                isFinalResult = true
                            )
                        }
                    )
                }
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

        KeyIndex_MemoryClear -> { // "MC"
            vibrateOnClick()
            CoroutineScope(Dispatchers.Default).launch {
                historyDao.deleteAllMemory()
                viewStates.value = viewStates.value.copy(memoryData = listOf(), isShowMemoryScreen = false)
            }
        }
        KeyIndex_MemoryRead -> { // "MR"
            vibrateOnClick()
            val firstValue = viewStates.value.memoryData.firstOrNull()
            if (firstValue != null) {
                viewStates.value = viewStates.value.copy(inputValue = firstValue.inputValue, isFinalResult = false)
            }
        }
        KeyIndex_MemoryPlus -> { // "M+"
            vibrateOnClick()
            CoroutineScope(Dispatchers.Default).launch {
                var newValue: String? = viewStates.value.inputValue
                val firstValue = viewStates.value.memoryData.firstOrNull()
                if (firstValue != null) {
                    newValue = calculate(firstValue.inputValue, newValue ?: "0", Operator.ADD).getOrNull()?.toPlainString()
                }
                if (newValue != null) {
                    historyDao.insertMemory(MemoryData(inputValue = newValue!!))
                    val memoryDataList = historyDao.getAllMemory()
                    viewStates.value = viewStates.value.copy(memoryData = memoryDataList)
                }
            }
        }
        KeyIndex_MemoryMinus -> { // "M-"
            vibrateOnClick()
            CoroutineScope(Dispatchers.Default).launch {
                var newValue: String? = viewStates.value.inputValue
                val firstValue = viewStates.value.memoryData.firstOrNull()
                if (firstValue != null) {
                    newValue = calculate(firstValue.inputValue, newValue ?: "0", Operator.MINUS).getOrNull()?.toPlainString()
                }
                if (newValue != null) {
                    historyDao.insertMemory(MemoryData(inputValue = newValue!!))
                    val memoryDataList = historyDao.getAllMemory()
                    viewStates.value = viewStates.value.copy(memoryData = memoryDataList)
                }
            }
        }
        KeyIndex_MemorySave -> { // "MS"
            vibrateOnClick()
            CoroutineScope(Dispatchers.Default).launch {
                historyDao.insertMemory(MemoryData(inputValue = viewStates.value.inputValue))
                val memoryDataList = historyDao.getAllMemory()
                viewStates.value = viewStates.value.copy(memoryData = memoryDataList)
            }
        }
        KeyIndex_MemoryList -> { // "M∨"
            vibrateOnClick()

            toggleMemoryList(false, viewStates)

        }
        else -> {

        }
    }
}

private fun clickClear(viewStates: MutableState<StandardState>) {
    isInputSecondValue = false
    isCalculated = false
    isAdvancedCalculated = false
    isErr = false
    viewStates.value = StandardState(memoryData = viewStates.value.memoryData)
}

private fun clickReciprocal(viewStates: MutableState<StandardState>) {
    viewStates.value.coroutineScope?.launch {
        syncCalculate("1", viewStates.value.inputValue, Operator.Divide) { result ->
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
    }
}

private fun clickSqrt(viewStates: MutableState<StandardState>) {
    viewStates.value.coroutineScope?.launch {
        syncCalculate(viewStates.value.inputValue, "0", Operator.SQRT) { result ->
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
    }
}

private fun clickPow2(viewStates: MutableState<StandardState>) {
    viewStates.value.coroutineScope?.launch {
        syncCalculate(viewStates.value.inputValue, "0", Operator.POW2) { result ->
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
    }
}

private fun clickEqual(viewStates: MutableState<StandardState>) {
    val inputValueCache = viewStates.value.inputValue

    if (viewStates.value.inputOperator == Operator.NUll) { // 没有添加操作符
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
        onCalculateFinish(viewStates, inputValueCache)
    }
    else { // 添加了操作符
        viewStates.value.coroutineScope?.launch {
            val calValue1: String
            val calValue2: String
            if (isCalculated) {
                calValue1 = viewStates.value.inputValue
                calValue2 = viewStates.value.lastInputValue
            }
            else {
                calValue1 = viewStates.value.lastInputValue
                calValue2 = viewStates.value.inputValue
            }

            syncCalculate(calValue1, calValue2, viewStates.value.inputOperator) { result ->
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
                        if (isCalculated) { // 当前已经计算过结果
                            viewStates.value = viewStates.value.copy(
                                inputValue = resultText,
                                showText = "$inputValue${viewStates.value.inputOperator.showText}${viewStates.value.lastInputValue}=",
                                isFinalResult = true,
                                lastShowText =
                                if (!isAdvancedCalculated)
                                    viewStates.value.showText+viewStates.value.inputValue
                                else viewStates.value.lastShowText
                            )
                        }
                        else { // 这是第一次计算
                            viewStates.value = viewStates.value.copy(
                                inputValue = resultText,
                                showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}$inputValue=",
                                isFinalResult = true,
                                lastInputValue = viewStates.value.inputValue,
                            )
                        }
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

                onCalculateFinish(viewStates, inputValueCache)
            }
        }
    }
}

private fun onCalculateFinish(viewStates: MutableState<StandardState>, inputValueCache: String) {
    isAdvancedCalculated = false

    CoroutineScope(Dispatchers.Default).launch {
        withContext(Dispatchers.Default) {
            if (!isErr) {  // 不保存错误结果
                historyDao.insert(
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

private fun toggleMemoryList(forceClose: Boolean, viewStates: MutableState<StandardState>) {
    if (forceClose) {
        viewStates.value = viewStates.value.copy(isShowMemoryScreen = false)
    }
    else {
        viewStates.value = viewStates.value.copy(isShowMemoryScreen = !viewStates.value.isShowMemoryScreen)
    }
}

data class StandardState(
    /** 当前输入的值 */
    val inputValue: String = "0",
    /** 输入的操作符 */
    val inputOperator: Operator = Operator.NUll,
    /** 上次输入的值 */
    val lastInputValue: String = "",
    /** 结果区展示的字符 */
    val showText: String = "",
    val isFinalResult: Boolean = false,
    val historyList: List<HistoryData> = listOf(),
    /** 计算历史展示的字符 */
    val lastShowText: String = "",
    /** 当前记忆数据 */
    val memoryData: List<MemoryData> = listOf(),
    /** 是否显示记忆数据 */
    val isShowMemoryScreen: Boolean = false,
    val coroutineScope: CoroutineScope? = null,
)

sealed class StandardAction {
    data class ToggleHistory(val forceClose: Boolean = false): StandardAction()
    data class ToggleMemoryScreen(val forceClose: Boolean = false): StandardAction()
    data class ClickBtn(val no: Int): StandardAction()
    data class ReadFromHistory(val item: HistoryData): StandardAction()
    data class DeleteHistory(val item: HistoryData?): StandardAction()
    data class DeleteMemoryItem(val item: MemoryData): StandardAction()
    data class Init(val coroutineScope: CoroutineScope): StandardAction()
    data class OnHoldPress(val isPress: Boolean, val no: Int): StandardAction()
}
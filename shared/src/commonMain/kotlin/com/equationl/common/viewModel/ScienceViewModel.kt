package com.equationl.common.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.equationl.common.constant.HoldPressMinInterval
import com.equationl.common.constant.HoldPressStartTime
import com.equationl.common.dataModel.KeyIndex_0
import com.equationl.common.dataModel.KeyIndex_10PowX
import com.equationl.common.dataModel.KeyIndex_2PowX
import com.equationl.common.dataModel.KeyIndex_9
import com.equationl.common.dataModel.KeyIndex_Abs
import com.equationl.common.dataModel.KeyIndex_Add
import com.equationl.common.dataModel.KeyIndex_ArcCos
import com.equationl.common.dataModel.KeyIndex_ArcCosH
import com.equationl.common.dataModel.KeyIndex_ArcCot
import com.equationl.common.dataModel.KeyIndex_ArcCotH
import com.equationl.common.dataModel.KeyIndex_ArcCsc
import com.equationl.common.dataModel.KeyIndex_ArcCscH
import com.equationl.common.dataModel.KeyIndex_ArcSec
import com.equationl.common.dataModel.KeyIndex_ArcSecH
import com.equationl.common.dataModel.KeyIndex_ArcSin
import com.equationl.common.dataModel.KeyIndex_ArcSinH
import com.equationl.common.dataModel.KeyIndex_ArcTan
import com.equationl.common.dataModel.KeyIndex_ArcTanH
import com.equationl.common.dataModel.KeyIndex_Back
import com.equationl.common.dataModel.KeyIndex_CE_Clear
import com.equationl.common.dataModel.KeyIndex_Ceil
import com.equationl.common.dataModel.KeyIndex_Constant_E
import com.equationl.common.dataModel.KeyIndex_Constant_PI
import com.equationl.common.dataModel.KeyIndex_Cos
import com.equationl.common.dataModel.KeyIndex_CosH
import com.equationl.common.dataModel.KeyIndex_Cot
import com.equationl.common.dataModel.KeyIndex_CotH
import com.equationl.common.dataModel.KeyIndex_Csc
import com.equationl.common.dataModel.KeyIndex_CscH
import com.equationl.common.dataModel.KeyIndex_Deg
import com.equationl.common.dataModel.KeyIndex_Divide
import com.equationl.common.dataModel.KeyIndex_Dms
import com.equationl.common.dataModel.KeyIndex_EPowX
import com.equationl.common.dataModel.KeyIndex_Equal
import com.equationl.common.dataModel.KeyIndex_Exp
import com.equationl.common.dataModel.KeyIndex_Factorial
import com.equationl.common.dataModel.KeyIndex_Floor
import com.equationl.common.dataModel.KeyIndex_LeftBrackets
import com.equationl.common.dataModel.KeyIndex_Ln
import com.equationl.common.dataModel.KeyIndex_Log
import com.equationl.common.dataModel.KeyIndex_LogYX
import com.equationl.common.dataModel.KeyIndex_MemoryClear
import com.equationl.common.dataModel.KeyIndex_MemoryList
import com.equationl.common.dataModel.KeyIndex_MemoryMinus
import com.equationl.common.dataModel.KeyIndex_MemoryPlus
import com.equationl.common.dataModel.KeyIndex_MemoryRead
import com.equationl.common.dataModel.KeyIndex_MemorySave
import com.equationl.common.dataModel.KeyIndex_Minus
import com.equationl.common.dataModel.KeyIndex_Mod
import com.equationl.common.dataModel.KeyIndex_Multiply
import com.equationl.common.dataModel.KeyIndex_NegativeNumber
import com.equationl.common.dataModel.KeyIndex_Point
import com.equationl.common.dataModel.KeyIndex_Pow2
import com.equationl.common.dataModel.KeyIndex_Pow3
import com.equationl.common.dataModel.KeyIndex_Random
import com.equationl.common.dataModel.KeyIndex_Reciprocal
import com.equationl.common.dataModel.KeyIndex_RightBrackets
import com.equationl.common.dataModel.KeyIndex_Sec
import com.equationl.common.dataModel.KeyIndex_SecH
import com.equationl.common.dataModel.KeyIndex_Sin
import com.equationl.common.dataModel.KeyIndex_SinH
import com.equationl.common.dataModel.KeyIndex_Sqrt
import com.equationl.common.dataModel.KeyIndex_Sqrt3
import com.equationl.common.dataModel.KeyIndex_Tan
import com.equationl.common.dataModel.KeyIndex_TanH
import com.equationl.common.dataModel.KeyIndex_ToggleAngle
import com.equationl.common.dataModel.KeyIndex_ToggleResultType
import com.equationl.common.dataModel.KeyIndex_XPowY
import com.equationl.common.dataModel.KeyIndex_XSqrtY
import com.equationl.common.dataModel.MemoryData
import com.equationl.common.dataModel.ScienceHistoryData
import com.equationl.common.dataModel.ScienceOperator
import com.equationl.common.database.HistoryDb
import com.equationl.common.platform.vibrateOnClear
import com.equationl.common.platform.vibrateOnClick
import com.equationl.common.platform.vibrateOnEqual
import com.equationl.common.platform.vibrateOnError
import com.equationl.common.utils.ScienceCalculate
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
import org.jetbrains.compose.resources.getString
import kotlin.random.Random

// TODO 科学计算的 viewModel


private var holdPressJob: Job? = null

@Composable
fun sciencePresenter(
    scienceActionFlow: Flow<ScienceAction>
): ScienceState {
    val scienceState = remember { mutableStateOf(ScienceState()) }

    LaunchedEffect(Unit) {
        scienceActionFlow.collect { action ->
            when (action) {
                is ScienceAction.ClickBtn -> clickBtn(action.no, scienceState)
                is ScienceAction.ToggleHistory -> toggleHistory(action.forceClose, scienceState)
                is ScienceAction.ToggleMemoryScreen -> toggleMemoryList(action.forceClose, scienceState)
                is ScienceAction.ReadFromHistory -> readFromHistory(action.item, scienceState)
                is ScienceAction.DeleteHistory -> deleteHistory(action.item, scienceState)
                is ScienceAction.DeleteMemoryItem -> deleteMemoryItem(action.item, scienceState)
                is ScienceAction.MemoryOperation -> memoryOperation(scienceState, action.operator, action.value)
                is ScienceAction.Init -> init(action.coroutineScope, scienceState)
                is ScienceAction.ChangeClearType -> changeClearType(action.type, scienceState)
                is ScienceAction.ChangeMoreFunctionShowType -> changeMoreFunctionShowType(action.type, scienceState)
                is ScienceAction.OnHoldPress -> {
                    holdPressJob?.cancel()
                    holdPressJob = launch {
                        onHoldPress(action.isPress, action.no, scienceState)
                    }
                }
            }
        }
    }

    return scienceState.value
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

private val historyDao = HistoryDb.instance.scienceHistory()
private val memoryDao = HistoryDb.instance.memory()

private fun init(coroutineScope: CoroutineScope, viewStates: MutableState<ScienceState>) {
    CoroutineScope(Dispatchers.Default).launch {
        val memoryData = memoryDao.getAllMemory()
        viewStates.value = viewStates.value.copy(coroutineScope = coroutineScope, memoryData = memoryData)
    }
}

private fun changeMoreFunctionShowType(type: Int, viewStates: MutableState<ScienceState>) {
    viewStates.value = viewStates.value.copy(moreFunctionShowType = type)
}

private fun changeClearType(type: Int, viewStates: MutableState<ScienceState>) {
    viewStates.value = viewStates.value.copy(clearType = type)
}

private suspend fun toggleHistory(forceClose: Boolean, viewStates: MutableState<ScienceState>) {
    vibrateOnClick()

    if (viewStates.value.historyList.isNotEmpty() || forceClose) {
        viewStates.value = viewStates.value.copy(historyList = listOf())
    }
    else {
        viewStates.value = viewStates.value.copy(historyList = listOf(
            ScienceHistoryData(-1, showText = getString(Res.string.loading), "null", "null", ScienceOperator.NUll, getString(Res.string.please_wait))
        ))

        CoroutineScope(Dispatchers.Default).launch {
            var list = historyDao.getAll()
            if (list.isEmpty()) {
                list = listOf(
                    ScienceHistoryData(-1, showText = "", "null", "null", ScienceOperator.NUll, getString(Res.string.history_is_empty))
                )
            }
            viewStates.value = viewStates.value.copy(historyList = list)
        }
    }
}

private fun readFromHistory(item: ScienceHistoryData, viewStates: MutableState<ScienceState>) {
    if (item.id != -1) {
        vibrateOnEqual()
        viewStates.value = ScienceState(
            inputValue = item.result,
            lastInputValue = item.lastInputText,
            inputOperator = item.operator,
            showText = item.showText,
            isFinalResult = true
        )
    }
}

private fun deleteHistory(item: ScienceHistoryData?, viewStates: MutableState<ScienceState>) {
    CoroutineScope(Dispatchers.Default).launch {
        vibrateOnError()
        viewStates.value = if (item == null) {
            historyDao.deleteAll()
            viewStates.value.copy(historyList = listOf())
        } else {
            vibrateOnClick()
            historyDao.delete(item)
            val newList = mutableListOf<ScienceHistoryData>()
            newList.addAll(viewStates.value.historyList)
            newList.remove(item)

            viewStates.value.copy(historyList = newList)
        }
    }
}

private fun deleteMemoryItem(item: MemoryData, viewStates: MutableState<ScienceState>) {
    CoroutineScope(Dispatchers.Default).launch {
        memoryDao.deleteMemory(item)
        val newList = viewStates.value.memoryData - item
        viewStates.value = viewStates.value.copy(memoryData = newList, isShowMemoryScreen = newList.isNotEmpty())
    }
}

private suspend fun onHoldPress(isPress: Boolean, no: Int, viewStates: MutableState<ScienceState>) {
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


private fun clickBtn(no: Int, viewStates: MutableState<ScienceState>) {
    if (isErr) {
        viewStates.value = ScienceState()
        isErr = false
        isAdvancedCalculated = false
        isCalculated = false
        isInputSecondValue = false
    }

    if (no in KeyIndex_0..KeyIndex_9) {
        vibrateOnClick()
        val newValue =
            if (viewStates.value.inputValue == "0") {
                if (viewStates.value.inputOperator != ScienceOperator.NUll) isInputSecondValue = true
                if (isAdvancedCalculated && viewStates.value.inputOperator == ScienceOperator.NUll) {  // 如果在输入高级运算符后直接输入数字，则重置状态
                    isAdvancedCalculated = false
                    isCalculated = false
                    isInputSecondValue = false
                    viewStates.value = ScienceState()
                    no.toString()
                }
                no.toString()
            }
            else if (viewStates.value.inputOperator != ScienceOperator.NUll && !isInputSecondValue) {
                isCalculated = false
                isInputSecondValue = true
                no.toString()
            }
            else if (isCalculated) {
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = ScienceState(
                    lastShowText =
                        if (!isAdvancedCalculated)
                            viewStates.value.showText+viewStates.value.inputValue
                        else viewStates.value.lastShowText
                )
                no.toString()
            }
            else if (isAdvancedCalculated && viewStates.value.inputOperator == ScienceOperator.NUll) { // 如果在输入高级运算符后直接输入数字，则重置状态
                isAdvancedCalculated = false
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = ScienceState()
                no.toString()
            }
            else if (!isCalculated && isInputSecondValue && isNeedClrInput) {
                isNeedClrInput = false
                no.toString()
            }
            else viewStates.value.inputValue + no.toString()

        viewStates.value = viewStates.value.copy(inputValue = newValue, isFinalResult = false)
    }

    if (no in KeyIndex_Sin..KeyIndex_ArcCotH) {
        val operator = when (no) {
            KeyIndex_Sin -> ScienceOperator.Sin
            KeyIndex_Cos -> ScienceOperator.Cos
            KeyIndex_Tan -> ScienceOperator.Tan
            KeyIndex_Sec -> ScienceOperator.Sec
            KeyIndex_Csc -> ScienceOperator.Csc
            KeyIndex_Cot -> ScienceOperator.Cot
            KeyIndex_ArcSin -> ScienceOperator.ArcSin
            KeyIndex_ArcCos -> ScienceOperator.ArcCos
            KeyIndex_ArcTan -> ScienceOperator.ArcTan
            KeyIndex_ArcSec -> ScienceOperator.ArcSec
            KeyIndex_ArcCsc -> ScienceOperator.ArcCsc
            KeyIndex_ArcCot -> ScienceOperator.ArcCot
            KeyIndex_SinH -> ScienceOperator.SinH
            KeyIndex_CosH -> ScienceOperator.CosH
            KeyIndex_TanH -> ScienceOperator.TanH
            KeyIndex_SecH -> ScienceOperator.SecH
            KeyIndex_CscH -> ScienceOperator.CscH
            KeyIndex_CotH -> ScienceOperator.CotH
            KeyIndex_ArcSinH -> ScienceOperator.ArcSinH
            KeyIndex_ArcCosH -> ScienceOperator.ArcCosH
            KeyIndex_ArcTanH -> ScienceOperator.ArcTanH
            KeyIndex_ArcSecH -> ScienceOperator.ArcSecH
            KeyIndex_ArcCscH -> ScienceOperator.ArcCscH
            KeyIndex_ArcCotH -> ScienceOperator.ArcCotH
            else -> ScienceOperator.NUll
        }
        clickInnerOperation(viewStates, viewStates.value.inputValue, "0", operator)
    }

    when (no) {
        KeyIndex_ToggleAngle -> {
            // 切换角度模式
            vibrateOnClick()

            var newValue = viewStates.value.angleType + 1
            if (newValue > 2) {
                newValue = 0
            }

            viewStates.value = viewStates.value.copy(
                angleType = newValue
            )
        }
        KeyIndex_ToggleResultType -> {
            // TODO 切换结果类型
            vibrateOnClick()

            var newValue = viewStates.value.resultType + 1
            if (newValue > 1) {
                newValue = 0
            }

            viewStates.value = viewStates.value.copy(
                resultType = newValue
            )
        }
        KeyIndex_Constant_PI, KeyIndex_Constant_E, KeyIndex_Random -> {
            vibrateOnClick()
            clickConst(viewStates, no)
        }
        KeyIndex_CE_Clear -> {
            vibrateOnClear()
            if (viewStates.value.clearType == 0) {
                clickClear(viewStates)
            }
            else {
                if (isCalculated) {
                    clickClear(viewStates)
                }
                else {
                    viewStates.value = viewStates.value.copy(inputValue = "0")
                }
            }
        }
        KeyIndex_Back -> { // "←"
            vibrateOnClick()
            if (viewStates.value.inputValue != "0") {
                var newValue = viewStates.value.inputValue.substring(0, viewStates.value.inputValue.length - 1)
                if (newValue.isEmpty()) newValue = "0"
                viewStates.value = viewStates.value.copy(inputValue = newValue)
            }
        }

        KeyIndex_Pow2 -> { // "x²"
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Pow2)
        }
        KeyIndex_Pow3 -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Pow3)
        }
        KeyIndex_Reciprocal -> { // "1/x"
            vibrateOnClick()
            clickInnerOperation(viewStates, "1", viewStates.value.inputValue, ScienceOperator.Reciprocal)
        }
        KeyIndex_Abs -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Abs)
        }
        KeyIndex_Exp -> {
            // TODO Exp 计算
        }
        KeyIndex_Mod -> {
            clickArithmetic(ScienceOperator.Mod, viewStates)
        }
        KeyIndex_Sqrt -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Sqrt)
        }
        KeyIndex_Sqrt3 -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Sqrt3)
        }
        KeyIndex_LeftBrackets -> {
            // TODO 左括号
        }
        KeyIndex_RightBrackets -> {
            // TODO 右括号
        }
        KeyIndex_Factorial -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Factorial)
        }
        KeyIndex_Divide -> { // "÷"
            clickArithmetic(ScienceOperator.Divide, viewStates)
        }
        KeyIndex_XPowY -> {
            clickArithmetic(ScienceOperator.XPowY, viewStates)
        }
        KeyIndex_XSqrtY -> {
            clickArithmetic(ScienceOperator.XSqrtY, viewStates)
        }
        KeyIndex_Multiply -> { // "×"
            clickArithmetic(ScienceOperator.MULTIPLY, viewStates)
        }
        KeyIndex_10PowX -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Op10PowX)
        }
        KeyIndex_2PowX -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Op2PowX)
        }
        KeyIndex_Minus -> { // "-"
            clickArithmetic(ScienceOperator.MINUS, viewStates)
        }
        KeyIndex_Log -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Log)
        }
        KeyIndex_LogYX -> {
            clickArithmetic(ScienceOperator.LogYX, viewStates)
        }
        KeyIndex_Add -> { // "+"
            clickArithmetic(ScienceOperator.ADD, viewStates)
        }
        KeyIndex_Ln -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Ln)
        }
        KeyIndex_EPowX -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.EPowX)
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
        KeyIndex_Equal -> { // "="
            clickEqual(viewStates)
        }
        KeyIndex_Floor -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Floor)
        }
        KeyIndex_Ceil -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Ceil)
        }
        KeyIndex_Dms -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.Dms)
        }
        KeyIndex_Deg -> {
            vibrateOnClick()
            clickInnerOperation(viewStates, viewStates.value.inputValue, "0", ScienceOperator.DEG)

        }

        KeyIndex_MemoryClear -> { // "MC"
            vibrateOnClick()
            CoroutineScope(Dispatchers.Default).launch {
                memoryDao.deleteAllMemory()
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
            memoryOperation(viewStates, ScienceOperator.ADD)
        }
        KeyIndex_MemoryMinus -> { // "M-"
            vibrateOnClick()
            memoryOperation(viewStates, ScienceOperator.MINUS)
        }
        KeyIndex_MemorySave -> { // "MS"
            vibrateOnClick()
            CoroutineScope(Dispatchers.Default).launch {
                memoryDao.insertMemory(MemoryData(inputValue = viewStates.value.inputValue))
                val memoryDataList = memoryDao.getAllMemory()
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

/**
 * 内存数据运算（M+、M-），如果 [value] 为  null 且当前储存的数据为空 则会新建一条数据，否则会更新传入的 [value]
 *
 * @param value 计算使用的数据，如果为 NUll 则使用当前储存的第一条数据，否则使用传入的数据
 * */
private fun memoryOperation(viewStates: MutableState<ScienceState>, operator: ScienceOperator, value: MemoryData? = null) {
    CoroutineScope(Dispatchers.Default).launch {
        var inputValue: String? = viewStates.value.inputValue
        val memoryValue = value ?: viewStates.value.memoryData.firstOrNull()

        if (memoryValue == null) {
            memoryDao.insertMemory(MemoryData(inputValue = inputValue!!))
        }
        else {
            inputValue = ScienceCalculate.calculate(memoryValue.inputValue, inputValue ?: "0", operator).getOrNull()?.toPlainString()
            memoryDao.updateMemory(memoryValue.copy(inputValue = inputValue!!))
        }

        val memoryDataList = memoryDao.getAllMemory()
        viewStates.value = viewStates.value.copy(memoryData = memoryDataList)
    }
}

private fun clickClear(viewStates: MutableState<ScienceState>) {
    isInputSecondValue = false
    isCalculated = false
    isAdvancedCalculated = false
    isErr = false
    viewStates.value = ScienceState(memoryData = viewStates.value.memoryData)
}

private fun clickInnerOperation(viewStates: MutableState<ScienceState>, leftValue: String, rightValue: String, operator: ScienceOperator) {
    viewStates.value.coroutineScope?.launch {
        ScienceCalculate.syncCalculate(leftValue, rightValue, operator) { result ->
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
                    showText = "${viewStates.value.lastInputValue}${viewStates.value.inputOperator.showText}${operator.showTemp.replace("\${value}", leftValue)}",
                    isFinalResult = false,
                    lastShowText =
                        if (viewStates.value.showText.indexOf("=") != -1)
                            viewStates.value.showText+viewStates.value.inputValue
                        else viewStates.value.lastShowText
                )
            }
            else {
                viewStates.value = newState.copy(
                    inputOperator = ScienceOperator.NUll,
                    lastInputValue = resultText,
                    showText = operator.showTemp.replace("\${value}", leftValue),
                    isFinalResult = false
                )
                //isInputSecondValue = true
            }

            isAdvancedCalculated = true
        }
    }
}

private fun clickConst(viewStates: MutableState<ScienceState>, keyIndex: Int) {
    val inputValue = when (keyIndex) {
        KeyIndex_Constant_PI -> ScienceCalculate.PI
        KeyIndex_Constant_E -> ScienceCalculate.E
        KeyIndex_Random -> Random.nextDouble().toString()
        else -> ""
    }
    val newValue =
        if (viewStates.value.inputValue == "0") {
            if (viewStates.value.inputOperator != ScienceOperator.NUll) isInputSecondValue = true
            if (isAdvancedCalculated && viewStates.value.inputOperator == ScienceOperator.NUll) {  // 如果在输入高级运算符后直接输入数字，则重置状态
                isAdvancedCalculated = false
                isCalculated = false
                isInputSecondValue = false
                viewStates.value = ScienceState()
            }
            inputValue
        }
        else if (viewStates.value.inputOperator != ScienceOperator.NUll && !isInputSecondValue) {
            isCalculated = false
            isInputSecondValue = true
            inputValue
        }
        else if (isCalculated) {
            isCalculated = false
            isInputSecondValue = false
            viewStates.value = ScienceState(
                lastShowText =
                    if (!isAdvancedCalculated)
                        viewStates.value.showText+viewStates.value.inputValue
                    else viewStates.value.lastShowText
            )
            inputValue
        }
        else if (isAdvancedCalculated && viewStates.value.inputOperator == ScienceOperator.NUll) { // 如果在输入高级运算符后直接输入数字，则重置状态
            isAdvancedCalculated = false
            isCalculated = false
            isInputSecondValue = false
            viewStates.value = ScienceState()
            inputValue
        }
        else if (!isCalculated && isInputSecondValue && isNeedClrInput) {
            isNeedClrInput = false
            inputValue
        }
        else inputValue

    viewStates.value = viewStates.value.copy(inputValue = newValue, isFinalResult = false)
}

private fun clickEqual(viewStates: MutableState<ScienceState>) {
    val inputValueCache = viewStates.value.inputValue

    if (viewStates.value.inputOperator == ScienceOperator.NUll) { // 没有添加操作符
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

            ScienceCalculate.syncCalculate(calValue1, calValue2, viewStates.value.inputOperator) { result ->
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

private fun onCalculateFinish(viewStates: MutableState<ScienceState>, inputValueCache: String) {
    isAdvancedCalculated = false

    CoroutineScope(Dispatchers.Default).launch {
        withContext(Dispatchers.Default) {
            if (!isErr) {  // 不保存错误结果
                historyDao.insert(
                    ScienceHistoryData(
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

private fun clickArithmetic(operator: ScienceOperator, viewStates: MutableState<ScienceState>) {
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

        if (viewStates.value.inputOperator == ScienceOperator.NUll) {  // 第一次添加操作符
            newState = newState.copy(
                showText = "${viewStates.value.showText}${operator.showTemp}"
            )
        }
        else {  // 不是第一次添加操作符，则需要把计算结果置于左边，并去掉高级运算的符号
            isCalculated = false
            isInputSecondValue = false

            clickEqual(viewStates)

            newState = newState.copy(
                lastInputValue = viewStates.value.inputValue,
                showText = "${viewStates.value.inputValue}${operator.showTemp}",
                inputValue = viewStates.value.inputValue
            )
        }

    }
    else {
        if (viewStates.value.inputOperator == ScienceOperator.NUll) { // 第一次添加操作符
            newState = newState.copy(
                showText = "${viewStates.value.inputValue}${operator.showTemp}"
            )
        }
        else { // 不是第一次添加操作符
            isCalculated = false
            isInputSecondValue = true
            isNeedClrInput = true

            newState = newState.copy(
                lastInputValue = viewStates.value.inputValue,
                showText = "${viewStates.value.inputValue}${operator.showTemp}",
                inputValue = viewStates.value.inputValue
            )
        }
    }

    viewStates.value = newState
}

private fun toggleMemoryList(forceClose: Boolean, viewStates: MutableState<ScienceState>) {
    if (forceClose) {
        viewStates.value = viewStates.value.copy(isShowMemoryScreen = false)
    }
    else {
        viewStates.value = viewStates.value.copy(isShowMemoryScreen = !viewStates.value.isShowMemoryScreen)
    }
}

data class ScienceState(
    /** 当前输入的值 */
    val inputValue: String = "0",
    /** 输入的操作符 */
    val inputOperator: ScienceOperator = ScienceOperator.NUll,
    /** 上次输入的值 */
    val lastInputValue: String = "",
    /** 结果区展示的字符 */
    val showText: String = "",
    val isFinalResult: Boolean = false,
    val historyList: List<ScienceHistoryData> = listOf(),
    /** 计算历史展示的字符 */
    val lastShowText: String = "",
    /** 当前记忆数据 */
    val memoryData: List<MemoryData> = listOf(),
    /** 是否显示记忆数据 */
    val isShowMemoryScreen: Boolean = false,
    val coroutineScope: CoroutineScope? = null,
    /** 角度类型 */
    val angleType: Int = 0,
    /** 数值结果类型 0: 纯净文本 1: 科学计数法*/
    val resultType: Int = 0,
    /** 清除类型 */
    val clearType: Int = 0,
    /** 更多功能弹窗显示类型：0 不显示；1 显示三角函数；2 显示其他函数*/
    val moreFunctionShowType: Int = 0
)

sealed class ScienceAction {
    data class ToggleHistory(val forceClose: Boolean = false): ScienceAction()
    data class ToggleMemoryScreen(val forceClose: Boolean = false): ScienceAction()
    data class ClickBtn(val no: Int): ScienceAction()
    data class ReadFromHistory(val item: ScienceHistoryData): ScienceAction()
    data class DeleteHistory(val item: ScienceHistoryData?): ScienceAction()
    data class DeleteMemoryItem(val item: MemoryData): ScienceAction()
    data class MemoryOperation(val operator: ScienceOperator, val value: MemoryData? = null): ScienceAction()
    data class Init(val coroutineScope: CoroutineScope): ScienceAction()
    data class OnHoldPress(val isPress: Boolean, val no: Int): ScienceAction()
    data class ChangeClearType(val type: Int): ScienceAction()
    data class ChangeMoreFunctionShowType(val type: Int): ScienceAction()
}
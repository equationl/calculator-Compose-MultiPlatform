package com.equationl.common.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowLeft
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.dataModel.KeyIndex_MemoryClear
import com.equationl.common.dataModel.KeyIndex_MemoryRead
import com.equationl.common.dataModel.ScienceOperator
import com.equationl.common.dataModel.memoryForbidBtnOnNoData
import com.equationl.common.dataModel.memoryFunctionKeyBoardBtn
import com.equationl.common.dataModel.scienceKeyBoardBtn
import com.equationl.common.dataModel.scienceOtherFunctionKeyBoardBtn
import com.equationl.common.dataModel.scienceTrigonometricFunctionKeyBoardBtn
import com.equationl.common.theme.InputLargeFontSize
import com.equationl.common.theme.ShowNormalFontSize
import com.equationl.common.utils.formatNumber
import com.equationl.common.utils.onPointerEvent
import com.equationl.common.view.widgets.noRippleClickable
import com.equationl.common.view.widgets.scrollToLeftAnimation
import com.equationl.common.viewModel.ScienceAction
import com.equationl.common.viewModel.ScienceState
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.other_function
import com.equationl.shared.generated.resources.scroll_left
import com.equationl.shared.generated.resources.text_is_too_long
import com.equationl.shared.generated.resources.trigonometric_function
import kotlinx.coroutines.channels.Channel
import org.jetbrains.compose.resources.stringResource
import showDialog

// TODO 科学计算器界面
@Composable
fun ScienceScreen(
    channel: Channel<ScienceAction>,
    state: ScienceState
) {

    LaunchedEffect(state.inputValue) {
        if (state.inputValue == "0" || state.inputValue.isBlank()) {
            channel.trySend(ScienceAction.ChangeClearType(0))
        }
        else {
            channel.trySend(ScienceAction.ChangeClearType(1))
        }
    }

    // 显示数据
    ShowScreen(state) {
        channel.trySend(ScienceAction.ToggleHistory(it))
        channel.trySend(ScienceAction.ToggleMemoryScreen(it))
    }

    // 记忆按钮
    MemoryKeyBoard(
        isDataEmpty = state.memoryData.isEmpty(),
        onHoldPress = { isPress, btnIndex ->
            channel.trySend(ScienceAction.OnHoldPress(isPress, btnIndex))
        }
    )

    Spacer(modifier = Modifier.height(4.dp))

    // 更多功能按钮
    MoreFunctionButtonList(showType = state.moreFunctionShowType) {
        channel.trySend(ScienceAction.ChangeMoreFunctionShowType(it))
    }

    Divider(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 0.dp))

    // 键盘与历史记录
    Box(Modifier.fillMaxSize()) {
        val isShowKeyBoard = state.historyList.isEmpty()

        // 键盘
        ScienceKeyBoard(
            angleType = state.angleType,
            resultType = state.resultType,
            clearType = state.clearType,
            onHoldPress = { isPress, btnIndex ->
                channel.trySend(ScienceAction.OnHoldPress(isPress, btnIndex))
            }
        )

        // 历史记录列表
        AnimatedVisibility(
            visible = !isShowKeyBoard,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            ScienceHistoryWidget(
                historyList = state.historyList,
                onClick = { channel.trySend(ScienceAction.ReadFromHistory(it)) },
                onDelete = { channel.trySend(ScienceAction.DeleteHistory(it)) })
        }

        // 记忆数据列表
        AnimatedVisibility(
            visible = state.isShowMemoryScreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            MemoryDataWidget(
                dataList = state.memoryData,
                onClick = { channel.trySend(ScienceAction.ClickBtn(KeyIndex_MemoryRead)) },
                onDelete = {
                    if (it == null) {
                        channel.trySend(ScienceAction.ClickBtn(KeyIndex_MemoryClear))
                    }
                    else {
                        channel.trySend(ScienceAction.DeleteMemoryItem(it))
                    }
                },
                onAdd = { channel.trySend(ScienceAction.MemoryOperation(ScienceOperator.ADD, it)) },
                onMinus = { channel.trySend(ScienceAction.MemoryOperation(ScienceOperator.MINUS, it)) }
            )
        }

        // 更多功能列表
        AnimatedVisibility(
            visible = state.moreFunctionShowType != 0,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                elevation = 5.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                MoreFunctionWidget(
                    state.moreFunctionShowType,
                    onPress = { isHold, index ->
                        channel.trySend(ScienceAction.OnHoldPress(isHold, index))
                    }
                )
            }
        }
    }
}


@Composable
private fun ShowScreen(state: ScienceState, onToggleFloatScreen: (Boolean) -> Unit) {
    val inputScrollerState = rememberScrollState()
    val showTextScrollerState = rememberScrollState()
    val isShowTextTipIcon by remember { derivedStateOf { showTextScrollerState.value != showTextScrollerState.maxValue } }
    val isShowInputTipIcon by remember { derivedStateOf { inputScrollerState.value != inputScrollerState.maxValue } }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .noRippleClickable { onToggleFloatScreen(true) }
        ,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.End) {
            // 计算公式
            AnimatedContent(targetState = state.showText) { targetState: String ->
                Box{
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(vertical = 8.dp)
                            .padding(end = 8.dp)
                            .horizontalScroll(showTextScrollerState, reverseScrolling = true)
                    ) {
                        Text(
                            text = if (targetState.length > 3000) stringResource(Res.string.text_is_too_long) else targetState,
                            fontSize = ShowNormalFontSize,
                            fontWeight = FontWeight.Light,
                            color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                showDialog(targetState)
                            }
                        )
                    }

                    if (isShowTextTipIcon) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowLeft,
                            contentDescription = stringResource(Res.string.scroll_left),
                            modifier = Modifier.scale(1.5f).align(Alignment.CenterStart).absoluteOffset(x = scrollToLeftAnimation(-10f).dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            // 输入值或计算结果
            AnimatedContent(
                targetState = state.inputValue,
                transitionSpec = {
                    if (targetState.length > initialState.length) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut())
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut())
                    }.using(
                        SizeTransform(clip = false)
                    )
                }
            ) { targetState: String ->
                Box {
                    Row(modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(vertical = 8.dp)
                        .padding(end = 8.dp)
                        .horizontalScroll(inputScrollerState, reverseScrolling = true)
                    ) {
                        Text(
                            text = if (targetState.length > 3000) stringResource(Res.string.text_is_too_long) else targetState.formatNumber(formatDecimal = state.isFinalResult),
                            fontSize = InputLargeFontSize,
                            fontWeight = FontWeight.Bold,
                            color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                showDialog(targetState)
                            }
                        )
                        LaunchedEffect(Unit) {
                            inputScrollerState.scrollTo(0)
                        }
                    }

                    if (isShowInputTipIcon && state.inputValue.length > 1) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowLeft,
                            contentDescription = stringResource(Res.string.scroll_left),
                            modifier = Modifier.scale(2f).align(Alignment.CenterStart).absoluteOffset(x = scrollToLeftAnimation(-10f).dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScienceKeyBoard(
    angleType: Int,
    resultType: Int,
    clearType: Int,
    onHoldPress: (isPress: Boolean, btnIndex: Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (btnRow in scienceKeyBoardBtn(angleType = angleType, resultType = resultType, clearType = clearType)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                for (btn in btnRow) {
                    Row(modifier = Modifier.weight(1f)) {
                        KeyBoardButton(
                            text = btn.text,
                            onClick = {  },  // 这里不再单独处理，统一放到 onHoldPress 处理
                            onHoldPress = {
                                onHoldPress(it, btn.index)
                            },
                            backGround = btn.background,
                            paddingValues = PaddingValues(0.5.dp),
                            isFilled = btn.isFilled,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryKeyBoard(isDataEmpty: Boolean, onHoldPress: (isPress: Boolean, btnIndex: Int) -> Unit) {
    Column(modifier = Modifier.fillMaxHeight(0.1f)) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (btn in memoryFunctionKeyBoardBtn()) {
                TextKeyBoardButton(
                    text = btn.text,
                    onHoldPress = {
                        onHoldPress(it, btn.index)
                    },
                    isAvailable = if (isDataEmpty) btn.index !in memoryForbidBtnOnNoData else true,
                    textColor = btn.background,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MoreFunctionButtonList(showType: Int, changeShowType: (type: Int) -> Unit) {
    Column(modifier = Modifier.fillMaxHeight(0.1f)) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToggleButton(
                text = stringResource(Res.string.trigonometric_function),
                isExpand = showType == 1,
                onClick = {
                    changeShowType(if (showType == 1) 0 else 1)
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            ToggleButton(
                text = stringResource(Res.string.other_function),
                isExpand = showType == 2,
                onClick = {
                    changeShowType(if (showType == 2) 0 else 2)
                }
            )
        }
    }
}

@Composable
private fun ToggleButton(
    text: String,
    isExpand: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(text)
        Icon(
            Icons.AutoMirrored.Outlined.ArrowRight,
            contentDescription = null,
            modifier = Modifier.padding(start = 2.dp)
                .rotate(if (isExpand) 90f else 0f)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun KeyBoardButton(
    text: String,
    onClick: () -> Unit,
    onHoldPress: (isPress: Boolean) -> Unit,
    backGround: Color = Color.White,
    isFilled: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .onPointerEvent(PointerEventType.Press) {
                onHoldPress(true)
            }
            .onPointerEvent(PointerEventType.Release) {
                onHoldPress(false)
            },
        backgroundColor = if (isFilled) backGround else MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp,
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 16.sp, color = if (isFilled) Color.Unspecified else backGround)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun TextKeyBoardButton(
    text: String,
    onHoldPress: (isPress: Boolean) -> Unit,
    textColor: Color = Color.White,
    isAvailable: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { },
        modifier = modifier
            .fillMaxSize()
            //.padding(paddingValues)
            .onPointerEvent(PointerEventType.Press) {
                if (isAvailable) {
                    onHoldPress(true)
                }
            }
            .onPointerEvent(PointerEventType.Release) {
                if (isAvailable) {
                    onHoldPress(false)
                }
            },
        backgroundColor = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp,
        border = BorderStroke(0.dp, Color.Transparent),
        enabled = isAvailable
    ) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                fontSize = 14.sp,
                color = if (isAvailable) {
                    textColor
                } else {
                    if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
                }
            )
        }
    }
}

@Composable
private fun MoreFunctionWidget(
    showType: Int,
    onPress: (isPress: Boolean, btnIndex: Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.4f)
    ) {

        val data = when (showType) {
            1 -> scienceTrigonometricFunctionKeyBoardBtn()
            2 -> scienceOtherFunctionKeyBoardBtn()
            else -> listOf()
        }

        for (btnRow in data) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                for (btn in btnRow) {
                    Row(modifier = Modifier.weight(1f)) {
                        KeyBoardButton(
                            text = btn.text,
                            onClick = {  },  // 这里不再单独处理，统一放到 onHoldPress 处理
                            onHoldPress = {
                                onPress(it, btn.index)
                            },
                            backGround = btn.background,
                            paddingValues = PaddingValues(0.5.dp),
                            isFilled = btn.isFilled,
                        )
                    }
                }
            }
        }
    }
}
package com.equationl.common.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.dataModel.standardKeyBoardBtn
import com.equationl.common.theme.InputLargeFontSize
import com.equationl.common.theme.ShowNormalFontSize
import com.equationl.common.theme.ShowSmallFontSize
import com.equationl.common.utils.formatNumber
import com.equationl.common.utils.onPointerEvent
import com.equationl.common.view.widgets.AutoSizeText
import com.equationl.common.view.widgets.noRippleClickable
import com.equationl.common.view.widgets.scrollToLeftAnimation
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.StandardState
import kotlinx.coroutines.channels.Channel
import showDialog

@Composable
fun StandardScreen(
    channel: Channel<StandardAction>,
    state: StandardState
) {
    // 显示数据
    ShowScreen(state) {
        channel.trySend(StandardAction.ToggleHistory(it))
    }

    Divider(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 0.dp))

    // 键盘与历史记录
    Box(Modifier.fillMaxSize()) {
        val isShowKeyBoard = state.historyList.isEmpty()

        StandardKeyBoard(
            onClick = {
                channel.trySend(StandardAction.ClickBtn(it))
            },
            onHoldPress = { isPress, btnIndex ->
                channel.trySend(StandardAction.OnHoldPress(isPress, btnIndex))
            }
        )

        AnimatedVisibility(
            visible = !isShowKeyBoard,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            HistoryWidget(
                historyList = state.historyList,
                onClick = { channel.trySend(StandardAction.ReadFromHistory(it)) },
                onDelete = { channel.trySend(StandardAction.DeleteHistory(it)) })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ShowScreen(state: StandardState, onToggleHistory: (Boolean) -> Unit) {
    val inputScrollerState = rememberScrollState()
    val showTextScrollerState = rememberScrollState()
    val isShowTextTipIcon by remember { derivedStateOf { showTextScrollerState.value != showTextScrollerState.maxValue } }
    val isShowInputTipIcon by remember { derivedStateOf { inputScrollerState.value != inputScrollerState.maxValue } }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .noRippleClickable { onToggleHistory(true) }
        ,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // 上一个计算结果
        AnimatedContent(targetState = state.lastShowText) { targetState: String ->
            SelectionContainer {
                AutoSizeText(
                    text = targetState,
                    fontSize = ShowSmallFontSize,
                    fontWeight = FontWeight.Light,
                    color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 16.dp)
                        .alpha(0.5f),
                    minSize = 10.sp
                )
            }
        }

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
                            text = if (targetState.length > 3000) "数字过大，无法显示，请点击查看" else targetState,
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
                            imageVector = Icons.Outlined.ArrowLeft,
                            contentDescription = "scroll left",
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
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() with
                                slideOutVertically { height -> height } + fadeOut()
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
                            text = if (targetState.length > 3000) "数字过大，无法显示，请点击查看" else targetState.formatNumber(formatDecimal = state.isFinalResult),
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
                            imageVector = Icons.Outlined.ArrowLeft,
                            contentDescription = "scroll left",
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
private fun StandardKeyBoard(onClick: (index: Int) -> Unit, onHoldPress: (isPress: Boolean, btnIndex: Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (btnRow in standardKeyBoardBtn()) {
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
                            isFilled = btn.isFilled
                        )
                    }
                }
            }
        }
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
    paddingValues: PaddingValues = PaddingValues(0.dp)
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
            Text(text, fontSize = 32.sp, color = if (isFilled) Color.Unspecified else backGround)
        }
    }
}
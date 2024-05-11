package com.equationl.common.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.dataModel.InputBase
import com.equationl.common.dataModel.asciiForbidBtn
import com.equationl.common.dataModel.programmerFunctionKeyBoardBtn
import com.equationl.common.dataModel.programmerNumberKeyBoardBtn
import com.equationl.common.theme.InputLargeFontSize
import com.equationl.common.theme.InputNormalFontSize
import com.equationl.common.theme.InputTitleContentSize
import com.equationl.common.theme.ShowNormalFontSize
import com.equationl.common.utils.addLeadingZero
import com.equationl.common.utils.formatHexToAscii
import com.equationl.common.utils.formatNumber
import com.equationl.common.view.widgets.AutoSizeText
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.ProgrammerBitKeyBoard
import com.equationl.common.viewModel.ProgrammerLength
import com.equationl.common.viewModel.ProgrammerNumberKeyBoard
import com.equationl.common.viewModel.ProgrammerState
import kotlinx.coroutines.channels.Channel

@Composable
fun ProgrammerScreen(
    channel: Channel<ProgrammerAction>,
    state: ProgrammerState,
    keyType: Int
) {
    if (keyType == ProgrammerNumberKeyBoard) {
        NumberKeyBoardContent(channel, state)
    }
    else if (keyType == ProgrammerBitKeyBoard) {
        BitKeyBoardContent(channel, state)
    }
}

@Composable
private fun BitKeyBoardContent(
    channel: Channel<ProgrammerAction>,
    state: ProgrammerState,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // 显示数据
        Row(modifier = Modifier.weight(1f)) {
            CenterScreen(state, channel)
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(vertical = 16.dp, horizontal = 0.dp)
        )

        // 右侧键盘
        Row(modifier = Modifier.weight(1f)) {
            BitBoard(state, channel)
        }
    }
}

@Composable
private fun NumberKeyBoardContent(
    channel: Channel<ProgrammerAction>,
    state: ProgrammerState,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧键盘
        Row(modifier = Modifier.weight(1.3f)) {
            FunctionKeyBoard(state, channel)
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(vertical = 16.dp, horizontal = 0.dp)
        )

        // 显示数据
        Row(modifier = Modifier.weight(2f)) {
            CenterScreen(state, channel)
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(vertical = 16.dp, horizontal = 0.dp)
        )

        // 右侧键盘
        Row(modifier = Modifier.weight(1.5f)) {
            NumberBoard(state, channel)
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CenterScreen(state: ProgrammerState, channel: Channel<ProgrammerAction>) {
    var isAsciiOnFocus by remember { mutableStateOf(false) }
    val asciiTextFiledFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // 计算公式
            AnimatedContent(targetState = state.showText) { targetState: String ->
                SelectionContainer {
                    Text(
                        text = targetState,
                        modifier = Modifier.padding(8.dp),
                        fontSize = ShowNormalFontSize,
                        fontWeight = FontWeight.Light,
                        color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                    )
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
                Row(modifier = Modifier.padding(8.dp)) {
                    SelectionContainer {
                        AutoSizeText(
                            text = targetState.formatNumber(
                                formatDecimal = false, // 程序员计算没有小数
                                addSplitChar = if (state.inputBase == InputBase.DEC) "," else " ",
                                splitLength = if (state.inputBase == InputBase.HEX || state.inputBase == InputBase.BIN) 4 else 3,
                                isAddLeadingZero = false, // 即使是二进制，在输入时也不应该有前导0
                                formatInteger = true
                            ),
                            fontSize = InputLargeFontSize,
                            fontWeight = FontWeight.Bold,
                            color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }


        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(2.dp)
                    .clickable {
                        channel.trySend(ProgrammerAction.ChangeInputBase(InputBase.HEX))
                        focusManager.clearFocus()
                    }
            ) {
                Text(
                    text = "HEX",
                    fontSize =
                    if (state.inputBase == InputBase.HEX && !isAsciiOnFocus) InputTitleContentSize
                    else InputNormalFontSize,
                    fontWeight = if (state.inputBase == InputBase.HEX && !isAsciiOnFocus) FontWeight.Bold else null,
                    color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                )

                SelectionContainer {
                    Text(
                        text = state.inputHexText.formatNumber(
                            addSplitChar = " ",
                            splitLength = if (state.isShowAscii) 2 else 4,
                            isReverseSplit = state.isShowAscii,
                        ),
                        fontSize = InputNormalFontSize,
                        modifier = Modifier.padding(start = 8.dp),
                        color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                    )
                }
            }

            if (!state.isShowAscii) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(2.dp)
                        .clickable { channel.trySend(ProgrammerAction.ChangeInputBase(InputBase.DEC)) }
                ) {
                    Text(
                        text = "DEC",
                        fontSize =
                        if (state.inputBase == InputBase.DEC) InputTitleContentSize
                        else InputNormalFontSize,
                        fontWeight = if (state.inputBase == InputBase.DEC) FontWeight.Bold else null,
                        color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                    )

                    SelectionContainer {
                        Text(
                            text = state.inputDecText.formatNumber(),
                            fontSize = InputNormalFontSize,
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                        )
                    }

                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(2.dp)
                        .clickable { channel.trySend(ProgrammerAction.ChangeInputBase(InputBase.OCT)) }
                ) {
                    Text(
                        text = "OCT",
                        fontSize =
                        if (state.inputBase == InputBase.OCT) InputTitleContentSize
                        else InputNormalFontSize,
                        fontWeight = if (state.inputBase == InputBase.OCT) FontWeight.Bold else null,
                        color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                    )

                    SelectionContainer {
                        Text(
                            text = state.inputOctText.formatNumber(addSplitChar = " "),
                            fontSize = InputNormalFontSize,
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                        )
                    }

                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(2.dp)
                        .clickable { channel.trySend(ProgrammerAction.ChangeInputBase(InputBase.BIN)) }
                ) {
                    Text(
                        text = "BIN",
                        fontSize =
                        if (state.inputBase == InputBase.BIN) InputTitleContentSize
                        else InputNormalFontSize,
                        fontWeight = if (state.inputBase == InputBase.BIN) FontWeight.Bold else null,
                        color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                    )

                    SelectionContainer {
                        Text(
                            text = state.inputBinText.formatNumber(
                                addSplitChar = " ",
                                splitLength = 4,
                                isAddLeadingZero = state.inputBinText != "0"
                            ),
                            fontSize = InputNormalFontSize,
                            modifier = Modifier
                                .padding(start = 8.dp),
                            color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                        )
                    }
                }
            }
            if (state.isShowAscii) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(2.dp)
                ) {
                    Text(
                        text = "ASCII",
                        fontSize =
                        if (isAsciiOnFocus) InputTitleContentSize
                        else InputNormalFontSize,
                        fontWeight = if (isAsciiOnFocus) FontWeight.Bold else null,
                        color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary,
                        modifier = Modifier.clickable {
                            asciiTextFiledFocusRequester.requestFocus()
                        }
                    )

                    OutlinedTextField(
                        value = state.inputHexText.formatHexToAscii(),
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Ascii,
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = MaterialTheme.colors.background,
                            focusedBorderColor = MaterialTheme.colors.background,
                            unfocusedBorderColor = MaterialTheme.colors.background
                        ),
                        onValueChange = { value ->
                            channel.trySend(ProgrammerAction.ChangeAsciiValue(value))
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .focusRequester(asciiTextFiledFocusRequester)
                            .onFocusChanged {
                                isAsciiOnFocus = it.isFocused
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberBoard(state: ProgrammerState, channel: Channel<ProgrammerAction>) {

    Column(modifier = Modifier.fillMaxSize()) {
        for (btnRow in programmerNumberKeyBoardBtn()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (btn in btnRow) {
                    val isAvailable = if (btn.isAvailable) {
                        if (state.isShowAscii) {
                            btn.index !in asciiForbidBtn
                        }
                        else {
                            btn.index !in state.inputBase.forbidBtn
                        }
                    } else {
                        false
                    }

                    Row(modifier = Modifier.weight(1f)) {
                        KeyBoardButton(
                            text = btn.text,
                            onClick = { channel.trySend(ProgrammerAction.ClickBtn(btn.index)) },
                            onHoldPress = {
                                println("from callback = $it")
                                // FIXME 这里发送 RELEASE 事件会失败
                                val result = channel.trySend(ProgrammerAction.OnHoldPress(it, btn.index))
                                println("send result = $result")
                            },
                            isAvailable = isAvailable,
                            backGround = btn.background,
                            isFilled = btn.isFilled,
                            paddingValues = PaddingValues(0.5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BitBoard(state: ProgrammerState, channel: Channel<ProgrammerAction>) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        var pos = remember { 60 }

        repeat(4) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                repeat(4) {
                    Column(
                        modifier = Modifier.weight(1f).padding(8.dp)
                    ) {
                        KeyBoardBitButtonGroup(
                            text = state.inputBinText.addLeadingZero()
                                .substring(60 - pos until 64 - pos),
                            pos = pos,
                            programmerLength = state.currentLength
                        ) { groupIndex: Int, index: Int ->
                            channel.trySend(ProgrammerAction.ClickBitBtn(63 - (groupIndex + index)))
                        }

                        pos -= 4
                    }
                }
            }
        }
    }
}

@Composable
private fun FunctionKeyBoard(state: ProgrammerState, channel: Channel<ProgrammerAction>) {

    Column(modifier = Modifier.fillMaxSize()) {
        for (btnRow in programmerFunctionKeyBoardBtn()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (btn in btnRow) {
                    val isAvailable = if (btn.isAvailable) {
                        if (state.isShowAscii) {
                           btn.index !in asciiForbidBtn
                        }
                        else {
                            btn.index !in state.inputBase.forbidBtn
                        }
                    } else {
                        false
                    }

                    Row(modifier = Modifier.weight(1f)) {
                        KeyBoardButton(
                            text = btn.text,
                            onClick = { channel.trySend(ProgrammerAction.ClickBtn(btn.index)) },
                            isAvailable = isAvailable,
                            onHoldPress = {
                                channel.trySend(ProgrammerAction.OnHoldPress(it, btn.index))
                            },
                            backGround = btn.background,
                            isFilled = btn.isFilled,
                            paddingValues = PaddingValues(0.5.dp)
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
    isAvailable: Boolean = true,
    backGround: Color = Color.White,
    isFilled: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Card(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .onPointerEvent(PointerEventType.Press) {
                println("press from raw")
                if (isAvailable) {
                    onHoldPress(true)
                }
            }
            .onPointerEvent(PointerEventType.Release) {
                println("release from raw")
                if (isAvailable) {
                    onHoldPress(false)
                }
            },
        backgroundColor = if (isFilled) backGround else MaterialTheme.colors.surface,
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
                fontSize = 24.sp,
                color = if (isAvailable) {
                    if (isFilled) Color.Unspecified else backGround
                } else {
                    if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
                }
            )
        }
    }
}

@Composable
private fun KeyBoardBitButtonGroup(
    text: String = "0000",
    pos: Int = 0,
    programmerLength: ProgrammerLength,
    onClick: (groupIndex: Int, index: Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        val isShow = isShowBitGroup(pos, programmerLength)

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            text.forEachIndexed { index, c ->
                val color = if (isShow) {
                    if (c == '1') MaterialTheme.colors.primary else Color.Unspecified
                } else {
                    if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
                }

                Text(
                    text = c.toString(),
                    modifier = Modifier.clickable(enabled = isShow) {
                        onClick(pos, 3 - index)
                    },
                    color = color,
                    fontSize = 24.sp
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = pos.toString(),
                fontSize = 12.sp
            )
        }
    }
}

private fun isShowBitGroup(
    pos: Int,
    programmerLength: ProgrammerLength
): Boolean {
    val group1 = listOf(60, 56, 52, 48, 44, 40, 36, 32)
    val group2 = listOf(28, 24, 20, 16)
    val group3 = listOf(12, 8)

    if (pos in group1) {
        return programmerLength == ProgrammerLength.QWORD
    }

    if (pos in group2) {
        return programmerLength == ProgrammerLength.QWORD || programmerLength == ProgrammerLength.DWORD
    }

    if (pos in group3) {
        return programmerLength == ProgrammerLength.QWORD || programmerLength == ProgrammerLength.DWORD || programmerLength == ProgrammerLength.WORD
    }

    return true
}
package com.equationl.common.overlay


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.equationl.common.dataModel.overlayKeyBoardBtn
import com.equationl.common.theme.OverlayLargeTextSize
import com.equationl.common.theme.OverlayNormalTextSize
import com.equationl.common.utils.formatNumber
import com.equationl.common.view.widgets.scrollToLeftAnimation
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.StandardState
import com.equationl.common.viewModel.standardPresenter
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.back_app_home
import com.equationl.shared.generated.resources.change_size
import com.equationl.shared.generated.resources.change_transparency
import com.equationl.shared.generated.resources.close
import com.equationl.shared.generated.resources.scroll_left
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun OverlayScreen(overLayChannel: Channel<OverlayAction>) {
    val standardChannel = remember { Channel<StandardAction>() }
    val standardFlow = remember(standardChannel) { standardChannel.consumeAsFlow() }
    val standardState = standardPresenter(standardFlow)

    standardChannel.trySend(StandardAction.Init(rememberCoroutineScope()))


    Column(Modifier.fillMaxSize()) {
        // 菜单
        TopMenu(overLayChannel)

        // 显示数据
        ShowScreen(standardState)

        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp))

        // 键盘
        StandardKeyBoard(standardChannel)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TopMenu(overLayChannel: Channel<OverlayAction>) {
    val context = LocalContext.current

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                imageVector = Icons.Outlined.FormatSize,
                contentDescription = stringResource(Res.string.change_size),
                Modifier.clickable {
                    overLayChannel.trySend(OverlayAction.ClickAdjustSize)
                }
            )

            Icon(
                imageVector = Icons.Outlined.InvertColors,
                contentDescription = stringResource(Res.string.change_transparency),
                Modifier.clickable {
                    overLayChannel.trySend(OverlayAction.ClickAdjustAlpha)
                }
            )
        }

        Row {
            Icon(
                imageVector = Icons.Outlined.Fullscreen,
                contentDescription = stringResource(Res.string.back_app_home),
                Modifier.clickable {
                    overLayChannel.trySend(OverlayAction.ClickBackFullScreen(context))
                }
            )

            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(Res.string.close),
                Modifier.clickable {
                    overLayChannel.trySend(OverlayAction.ClickClose(context))
                }
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ShowScreen(viewState: StandardState) {
    val inputScrollerState = rememberScrollState()
    val showTextScrollerState = rememberScrollState()

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            if (showTextScrollerState.value != showTextScrollerState.maxValue) {
                Icon(
                    imageVector = Icons.Outlined.ArrowLeft,
                    contentDescription = stringResource(Res.string.scroll_left),
                    modifier = Modifier.absoluteOffset(x = scrollToLeftAnimation(-10f).dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
                    .horizontalScroll(showTextScrollerState, reverseScrolling = true)
            ) {
                Text(
                    text = viewState.showText,
                    fontSize = OverlayNormalTextSize,
                    fontWeight = FontWeight.Light,
                    color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            if (inputScrollerState.value != inputScrollerState.maxValue) {
                Icon(
                    imageVector = Icons.Outlined.ArrowLeft,
                    contentDescription = stringResource(Res.string.scroll_left),
                    modifier = Modifier.absoluteOffset(x = scrollToLeftAnimation(-10f).dp)
                )
            }

            Row(modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
                .horizontalScroll(inputScrollerState, reverseScrolling = true)
            ) {
                Text(
                    text = viewState.inputValue.formatNumber(formatDecimal = viewState.isFinalResult),
                    fontSize = OverlayLargeTextSize,
                    fontWeight = FontWeight.Bold,
                    color = if (MaterialTheme.colors.isLight) Color.Unspecified else MaterialTheme.colors.primary
                )
                LaunchedEffect(Unit) {
                    inputScrollerState.scrollTo(0)
                }
            }
        }
    }
}

@Composable
private fun StandardKeyBoard(standardChannel: Channel<StandardAction>) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (btnRow in overlayKeyBoardBtn()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                for (btn in btnRow) {
                    Row(modifier = Modifier.weight(1f)) {
                        KeyBoardButton(
                            text = btn.text,
                            onClick = { standardChannel.trySend(StandardAction.ClickBtn(btn.index)) },
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun KeyBoardButton(
    text: String,
    onClick: () -> Unit,
    backGround: Color = Color.White,
    isFilled: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        backgroundColor = if (isFilled) backGround else MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp,
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = OverlayLargeTextSize, color = if (isFilled) Color.Unspecified else backGround)
        }
    }
}

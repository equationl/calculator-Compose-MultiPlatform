package com.equationl.common.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.viewModel.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun HomeScreen(
    standardChannel: Channel<StandardAction>,
    standardState: StandardState,
    programmerChannel: Channel<ProgrammerAction>,
    programmerState: ProgrammerState
) {
    val channel = remember { Channel<HomeAction>() }
    val flow = remember(channel) { channel.consumeAsFlow() }
    val state = homePresenter(flow)

    Column(
        Modifier
            .fillMaxSize()
    ) {

        MenuTitle(
            keyBoardType = state.keyBoardType,
            isFloat = state.isFloat,
            onClickMenu = {
                channel.trySend(
                    HomeAction.ClickMenu(
                        changeToType = if (state.keyBoardType == KeyboardTypeStandard) KeyboardTypeProgrammer else KeyboardTypeStandard
                    )
                )
            },
            onClickHistory = {
                standardChannel.trySend(StandardAction.ToggleHistory())
            },
            onClickOverlay = {
                channel.trySend(HomeAction.ClickOverlay)
            }
        )

        if (state.keyBoardType == KeyboardTypeProgrammer) {
            ProgrammerScreen(programmerChannel, programmerState)
        }
        else {
            StandardScreen(standardChannel, standardState)
        }

    }
}

@Composable
private fun MenuTitle(
    keyBoardType: Int,
    isFloat: Boolean,
    onClickMenu: () -> Unit,
    onClickHistory: () -> Unit,
    onClickOverlay: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onClickMenu() }
        ) {
            Icon(imageVector = Icons.Outlined.ScreenRotation,
                contentDescription = "ScreenRotation",
                modifier = Modifier.padding(4.dp))
            Text(
                text = if (keyBoardType == KeyboardTypeProgrammer) "程序员" else "标准",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (keyBoardType == KeyboardTypeStandard) {
            Row {
                Icon(imageVector = Icons.Outlined.History,
                    contentDescription = "history",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickHistory() }
                )
                Icon(imageVector = if (isFloat) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "overlay View",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickOverlay() }
                )
            }
        }
    }
}
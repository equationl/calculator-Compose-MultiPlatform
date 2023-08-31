package com.equationl.common.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.viewModel.*
import kotlinx.coroutines.channels.Channel

@Composable
fun HomeScreen(
    homeChannel: Channel<HomeAction>,
    homeState: HomeState,
    standardChannel: Channel<StandardAction>,
    standardState: StandardState,
    programmerChannel: Channel<ProgrammerAction>,
    programmerState: ProgrammerState
) {
    Column(
        Modifier
            .fillMaxSize()
    ) {

        MenuTitle(
            keyBoardType = homeState.keyBoardType,
            programmerKeyBoardType = homeState.programmerKeyBoardType,
            isFloat = homeState.isFloat,
            onClickMenu = {
                homeChannel.trySend(
                    HomeAction.ClickMenu(
                        changeToType = if (homeState.keyBoardType == KeyboardTypeStandard) KeyboardTypeProgrammer else KeyboardTypeStandard,
                        isFromUser = true
                    )
                )
            },
            onClickHistory = {
                standardChannel.trySend(StandardAction.ToggleHistory())
            },
            onClickOverlay = {
                homeChannel.trySend(HomeAction.ClickOverlay)
            },
            onClickChangeKeyBoard = {
                homeChannel.trySend(HomeAction.OnChangeProgrammerKeyBoardType(it))
            }
        )

        if (homeState.keyBoardType == KeyboardTypeProgrammer) {
            ProgrammerScreen(programmerChannel, programmerState, homeState.programmerKeyBoardType)
        }
        else {
            StandardScreen(standardChannel, standardState)
        }

    }
}

@Composable
private fun MenuTitle(
    keyBoardType: Int,
    programmerKeyBoardType: Int,
    isFloat: Boolean,
    onClickMenu: () -> Unit,
    onClickHistory: () -> Unit,
    onClickOverlay: () -> Unit,
    onClickChangeKeyBoard: (type: Int) -> Unit,
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
        else if (keyBoardType == KeyboardTypeProgrammer) {
            Row {
                Icon(imageVector = Icons.Filled.Keyboard,
                    contentDescription = "number keyboard",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickChangeKeyBoard(ProgrammerNumberKeyBoard) },
                    tint = if (programmerKeyBoardType == ProgrammerNumberKeyBoard) MaterialTheme.colors.primary else Color.Unspecified
                )
                Icon(imageVector = Icons.Outlined.Apps,
                    contentDescription = "bit keyboard",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickChangeKeyBoard(ProgrammerBitKeyBoard) },
                    tint = if (programmerKeyBoardType == ProgrammerBitKeyBoard) MaterialTheme.colors.primary else Color.Unspecified
                )
            }
        }
    }
}
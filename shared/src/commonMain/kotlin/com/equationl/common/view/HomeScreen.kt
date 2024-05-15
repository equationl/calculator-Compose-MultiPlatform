package com.equationl.common.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.constant.PlatformType
import com.equationl.common.platform.currentPlatform
import com.equationl.common.platform.isNeedShowFloatBtn
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.HomeState
import com.equationl.common.viewModel.KeyboardTypeProgrammer
import com.equationl.common.viewModel.KeyboardTypeStandard
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.ProgrammerBitKeyBoard
import com.equationl.common.viewModel.ProgrammerLength
import com.equationl.common.viewModel.ProgrammerNumberKeyBoard
import com.equationl.common.viewModel.ProgrammerState
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.StandardState
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.bit_keyBoard
import com.equationl.shared.generated.resources.change_transparency
import com.equationl.shared.generated.resources.float_show
import com.equationl.shared.generated.resources.history
import com.equationl.shared.generated.resources.keyBoard_title_programmer
import com.equationl.shared.generated.resources.keyBoard_title_standard
import com.equationl.shared.generated.resources.number_keyBoard
import com.equationl.shared.generated.resources.screen_rotation
import com.equationl.shared.generated.resources.show_ascii
import kotlinx.coroutines.channels.Channel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

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
            programmerLength = programmerState.currentLength,
            isFloat = homeState.isFloat,
            isShowAscii = programmerState.isShowAscii,
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
            onClickToggleShowAscii = {
                programmerChannel.trySend(ProgrammerAction.ToggleShowAscii)
            },
            onClickChangeKeyBoard = {
                homeChannel.trySend(HomeAction.OnChangeProgrammerKeyBoardType(it))
            },
            onClickChangeProgrammerLength = {
                programmerChannel.trySend(ProgrammerAction.ClickChangeLength)
            },
            onClickChangeTransparency = {
                homeChannel.trySend(HomeAction.ChangeTransparency)
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

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MenuTitle(
    keyBoardType: Int,
    programmerKeyBoardType: Int,
    programmerLength: ProgrammerLength,
    isFloat: Boolean,
    isShowAscii: Boolean,
    onClickMenu: () -> Unit,
    onClickHistory: () -> Unit,
    onClickOverlay: () -> Unit,
    onClickToggleShowAscii: () -> Unit,
    onClickChangeKeyBoard: (type: Int) -> Unit,
    onClickChangeProgrammerLength: () -> Unit,
    onClickChangeTransparency: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onClickMenu() }
        ) {
            Icon(imageVector = Icons.Outlined.ScreenRotation,
                contentDescription = stringResource(Res.string.screen_rotation),
                modifier = Modifier.padding(4.dp))
            Text(
                text = if (keyBoardType == KeyboardTypeProgrammer) stringResource(Res.string.keyBoard_title_programmer) else stringResource(Res.string.keyBoard_title_standard),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (keyBoardType == KeyboardTypeStandard) {
            Row {
                Icon(imageVector = Icons.Outlined.History,
                    contentDescription = stringResource(Res.string.history),
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickHistory() }
                )

                if (isFloat && currentPlatform() == PlatformType.Desktop) {
                    Icon(imageVector = Icons.Outlined.InvertColors,
                        contentDescription = stringResource(Res.string.change_transparency),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onClickChangeTransparency() }
                    )
                }

                if (isNeedShowFloatBtn()) {
                    Icon(imageVector = if (isFloat) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = stringResource(Res.string.float_show),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onClickOverlay() }
                    )
                }
            }
        }
        else if (keyBoardType == KeyboardTypeProgrammer) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Keyboard,
                    contentDescription = stringResource(Res.string.number_keyBoard),
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickChangeKeyBoard(ProgrammerNumberKeyBoard) },
                    tint = if (programmerKeyBoardType == ProgrammerNumberKeyBoard) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant
                )
                Icon(imageVector = Icons.Outlined.Apps,
                    contentDescription = stringResource(Res.string.bit_keyBoard),
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickChangeKeyBoard(ProgrammerBitKeyBoard) },
                    tint = if (programmerKeyBoardType == ProgrammerBitKeyBoard) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = programmerLength.showText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 80.dp).clickable {
                        onClickChangeProgrammerLength()
                    }
                )

                Icon(imageVector = Icons.Filled.Abc,
                    contentDescription = stringResource(Res.string.show_ascii),
                    tint = if (isShowAscii) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onClickToggleShowAscii() }
                )

                if (isNeedShowFloatBtn()) {
                    Icon(imageVector = if (isFloat) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = stringResource(Res.string.float_show),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onClickOverlay() }
                    )
                }
            }
        }
    }
}
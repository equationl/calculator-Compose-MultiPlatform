package com.equationl.common.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.constant.KeyBoardTypeEnum
import com.equationl.common.constant.KeyBoardTypeEnum.Programmer
import com.equationl.common.constant.KeyBoardTypeEnum.Science
import com.equationl.common.constant.KeyBoardTypeEnum.Standard
import com.equationl.common.constant.PlatformType
import com.equationl.common.platform.currentPlatform
import com.equationl.common.platform.isNeedShowFloatBtn
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.HomeState
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.ProgrammerBitKeyBoard
import com.equationl.common.viewModel.ProgrammerLength
import com.equationl.common.viewModel.ProgrammerNumberKeyBoard
import com.equationl.common.viewModel.ProgrammerState
import com.equationl.common.viewModel.ScienceAction
import com.equationl.common.viewModel.ScienceState
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.StandardState
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.app_name
import com.equationl.shared.generated.resources.bit_keyBoard
import com.equationl.shared.generated.resources.change_transparency
import com.equationl.shared.generated.resources.float_show
import com.equationl.shared.generated.resources.history
import com.equationl.shared.generated.resources.keyBoard_title_programmer
import com.equationl.shared.generated.resources.keyBoard_title_science
import com.equationl.shared.generated.resources.keyBoard_title_standard
import com.equationl.shared.generated.resources.more
import com.equationl.shared.generated.resources.number_keyBoard
import com.equationl.shared.generated.resources.show_ascii
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    homeChannel: Channel<HomeAction>,
    homeState: HomeState,
    standardChannel: Channel<StandardAction>,
    standardState: StandardState,
    programmerChannel: Channel<ProgrammerAction>,
    programmerState: ProgrammerState,
    scienceChannel: Channel<ScienceAction>,
    scienceState: ScienceState
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            drawerContent(
                onClickMenu = { index ->
                    scope.launch {
                        drawerState.close()

                        homeChannel.trySend(
                            HomeAction.ClickMenu(
                                changeToType = index,
                                isFromUser = true
                            )
                        )
                    }
                }
            )
        }
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
                    scope.launch {
                        drawerState.open()
                    }
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

            when (homeState.keyBoardType) {
                Standard -> StandardScreen(standardChannel, standardState)
                Programmer -> ProgrammerScreen(programmerChannel, programmerState, homeState.programmerKeyBoardType)
                Science -> ScienceScreen(scienceChannel, scienceState)
            }
        }
    }
}

@Composable
private fun MenuTitle(
    keyBoardType: KeyBoardTypeEnum,
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
            Icon(imageVector = Icons.Outlined.Menu,
                contentDescription = stringResource(Res.string.more),
                modifier = Modifier.padding(4.dp))
            Text(
                text = getTitle(keyBoardType),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when (keyBoardType) {
            Standard -> TitleActionStandard(
                isFloat = isFloat,
                onClickHistory = onClickHistory,
                onClickOverlay = onClickOverlay,
                onClickChangeTransparency = onClickChangeTransparency,
            )
            Programmer -> TitleActionProgrammer(
                isFloat = isFloat,
                programmerKeyBoardType = programmerKeyBoardType,
                programmerLength = programmerLength,
                isShowAscii = isShowAscii,
                onClickChangeKeyBoard = onClickChangeKeyBoard,
                onClickChangeProgrammerLength = onClickChangeProgrammerLength,
                onClickToggleShowAscii = onClickToggleShowAscii,
                onClickOverlay = onClickOverlay
            )
            Science -> TitleActionScience()
        }
    }
}

@Composable
private fun drawerContent(
    onClickMenu: (index: Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp).padding(top = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.clickable {
                onClickMenu(1)
            }
        ) {
            Icon(imageVector = Icons.Outlined.Calculate,
                contentDescription = stringResource(Res.string.keyBoard_title_standard),
                modifier = Modifier.padding(4.dp))
            Text(
                text = stringResource(Res.string.keyBoard_title_standard),
                style = MaterialTheme.typography.body1,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.clickable {
                onClickMenu(3)
            }
        ) {
            Icon(imageVector = Icons.Outlined.Science,
                contentDescription = stringResource(Res.string.keyBoard_title_science),
                modifier = Modifier.padding(4.dp))
            Text(
                text = stringResource(Res.string.keyBoard_title_science),
                style = MaterialTheme.typography.body1,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.clickable {
                onClickMenu(2)
            }
        ) {
            Icon(imageVector = Icons.Outlined.Code,
                contentDescription = stringResource(Res.string.keyBoard_title_programmer),
                modifier = Modifier.padding(4.dp))
            Text(
                text = stringResource(Res.string.keyBoard_title_programmer),
                style = MaterialTheme.typography.body1,
            )
        }
    }
}

@Composable
private fun TitleActionStandard(
    isFloat:  Boolean,
    onClickHistory: () -> Unit,
    onClickChangeTransparency: () -> Unit,
    onClickOverlay: () -> Unit
) {
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

@Composable
private fun TitleActionProgrammer(
    isFloat: Boolean,
    onClickChangeKeyBoard: (type: Int) -> Unit,
    programmerKeyBoardType: Int,
    programmerLength: ProgrammerLength,
    isShowAscii: Boolean,
    onClickChangeProgrammerLength: () -> Unit,
    onClickToggleShowAscii: () -> Unit,
    onClickOverlay: () -> Unit
) {
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

@Composable
private fun TitleActionScience() {
 // TODO 科学计算器
}

@Composable
private fun getTitle(type: KeyBoardTypeEnum): String {
    return when (type) {
        Standard -> stringResource(Res.string.keyBoard_title_standard)
        Science -> stringResource(Res.string.keyBoard_title_science)
        Programmer -> stringResource(Res.string.keyBoard_title_programmer)
        else -> {
            ""
        }
    }
}
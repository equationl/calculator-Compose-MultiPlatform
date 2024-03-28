package com.equationl.common.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.equationl.common.platform.changeKeyBoardType
import com.equationl.common.platform.showFloatWindows
import com.equationl.common.platform.vibrateOnClick
import kotlinx.coroutines.flow.Flow

const val KeyboardTypeStandard = 1
const val KeyboardTypeProgrammer = 2

const val ProgrammerNumberKeyBoard = 3
const val ProgrammerBitKeyBoard = 4

@Composable
fun homePresenter(
    homeActionFlow: Flow<HomeAction>
): HomeState {
    var homeState by remember { mutableStateOf(HomeState()) }

    LaunchedEffect(Unit) {
        homeActionFlow.collect { action ->
            when (action) {
                is HomeAction.ClickMenu -> {
                    vibrateOnClick()

                    homeState = homeState.copy(keyBoardType = action.changeToType)

                    clickChangeKeyBoardType(action.changeToType, action.isFromUser)
                }
                is HomeAction.ClickOverlay -> {
                    vibrateOnClick()

                    if (homeState.isFloat) {
                        homeState = homeState.copy(isFloat = false)

                        clickOverlay()
                    }
                    else {
                        homeState = homeState.copy(isFloat = true)

                        clickOverlay()
                    }
                }

                is HomeAction.OnScreenOrientationChange -> {
                    homeState = homeState.copy(keyBoardType = action.changeToType)
                }

                is HomeAction.OnChangeProgrammerKeyBoardType -> {
                    vibrateOnClick()

                    homeState = homeState.copy(programmerKeyBoardType = action.newType)
                }

                is HomeAction.InitState -> {
                    homeState = homeState.copy(isFloat = action.isFloat, keyBoardType = action.boardType)
                }

                HomeAction.ChangeTransparency -> {
                    var alpha = homeState.transparency

                    alpha += 0.2f

                    if (alpha > 1f) {
                        alpha = 0.2f
                    }

                    homeState = homeState.copy(transparency = alpha)
                }
            }
        }
    }

    return homeState
}

private fun clickOverlay() {
    showFloatWindows()
}

private fun clickChangeKeyBoardType(changeToType: Int, isFromUser: Boolean) {
    changeKeyBoardType(changeToType, isFromUser)
}

data class HomeState(
    val keyBoardType: Int = KeyboardTypeStandard,
    val isFloat: Boolean = false,
    val programmerKeyBoardType: Int = ProgrammerNumberKeyBoard,
    val transparency: Float = 1f
)

sealed class HomeAction {
    data object ClickOverlay: HomeAction()
    data object ChangeTransparency: HomeAction()
    data class ClickMenu(val changeToType: Int, val isFromUser: Boolean): HomeAction()
    data class OnScreenOrientationChange(val changeToType: Int): HomeAction()
    data class OnChangeProgrammerKeyBoardType(val newType: Int): HomeAction()
    data class InitState(val isFloat: Boolean, val boardType: Int): HomeAction()
}
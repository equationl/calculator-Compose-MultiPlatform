package com.equationl.common.viewModel

import androidx.compose.runtime.*
import com.equationl.common.platform.changeKeyBoardType
import com.equationl.common.platform.showFloatWindows
import kotlinx.coroutines.flow.Flow

const val KeyboardTypeStandard = 1
const val KeyboardTypeProgrammer = 2

@Composable
fun homePresenter(
    homeActionFlow: Flow<HomeAction>
): HomeState {
    var homeState by remember { mutableStateOf(HomeState()) }

    LaunchedEffect(Unit) {
        homeActionFlow.collect { action ->
            when (action) {
                is HomeAction.ClickMenu -> {
                    homeState = homeState.copy(keyBoardType = action.changeToType)

                    clickChangeKeyBoardType(action.changeToType, action.isFromUser)
                }
                is HomeAction.ClickOverlay -> {
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
    val isFloat: Boolean = false
)

sealed class HomeAction {
    object ClickOverlay: HomeAction()
    data class ClickMenu(val changeToType: Int, val isFromUser: Boolean): HomeAction()
    data class OnScreenOrientationChange(val changeToType: Int): HomeAction()
}
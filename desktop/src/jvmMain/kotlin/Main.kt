import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.equationl.common.constant.Text
import com.equationl.common.utils.asciiCode2BtnIndex
import com.equationl.common.utils.isKeyTyped
import com.equationl.common.value.Config
import com.equationl.common.value.defaultWindowPosition
import com.equationl.common.value.defaultWindowSize
import com.equationl.common.value.landWindowSize
import com.equationl.common.viewModel.KeyboardTypeStandard
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.StandardAction
import kotlinx.coroutines.channels.Channel


fun main() = application {

    val state = if (Config.boardType.value == KeyboardTypeStandard) {
        rememberWindowState(size = defaultWindowSize, position = defaultWindowPosition)
    } else {
        rememberWindowState(size = landWindowSize, position = defaultWindowPosition)
    }


    if (Config.isFloat.value) {
        val standardChannel = remember { Channel<StandardAction>() }
        val programmerChannel = remember { Channel<ProgrammerAction>() }

        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = Text.AppName,
            icon = painterResource("icon.png"),
            alwaysOnTop = true,
            transparent = true,
            undecorated = true,
            onKeyEvent = {
                if (isKeyTyped(it)) {
                    val btnIndex = asciiCode2BtnIndex(it.utf16CodePoint)
                    if (btnIndex != -1) {
                        if (Config.boardType.value == KeyboardTypeStandard) {
                            standardChannel.trySend(StandardAction.ClickBtn(btnIndex))
                        }
                        else {
                            programmerChannel.trySend(ProgrammerAction.ClickBtn(btnIndex))
                        }
                    }
                }
                true
            }
        ) {
            APP(
                standardChannelTop = standardChannel,
                programmerChannelTop = programmerChannel,
                isFloat = Config.isFloat.value,
                boardType = Config.boardType.value,
            )
        }
    }
    else {
        val standardChannel = remember { Channel<StandardAction>() }
        val programmerChannel = remember { Channel<ProgrammerAction>() }

        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = Text.AppName,
            icon = painterResource("icon.png"),
            onKeyEvent = {
                if (isKeyTyped(it)) {
                    val btnIndex = asciiCode2BtnIndex(it.utf16CodePoint)
                    if (btnIndex != -1) {
                        if (Config.boardType.value == KeyboardTypeStandard) {
                            standardChannel.trySend(StandardAction.ClickBtn(btnIndex))
                        }
                        else {
                            programmerChannel.trySend(ProgrammerAction.ClickBtn(btnIndex))
                        }
                    }
                }
                true
            }
        ) {
            APP(
                standardChannelTop = standardChannel,
                programmerChannelTop = programmerChannel,
                isFloat = Config.isFloat.value,
                boardType = Config.boardType.value,
            )
        }
    }
}

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.equationl.common.constant.Text
import com.equationl.common.theme.CalculatorComposeTheme
import com.equationl.common.utils.asciiCode2BtnIndex
import com.equationl.common.utils.isKeyTyped
import com.equationl.common.value.Config
import com.equationl.common.value.defaultWindowPosition
import com.equationl.common.value.defaultWindowSize
import com.equationl.common.value.landWindowSize
import com.equationl.common.view.HomeScreen
import com.equationl.common.viewModel.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow


fun main() = application {

    val state = if (Config.boardType.value == KeyboardTypeStandard) {
        rememberWindowState(size = defaultWindowSize, position = defaultWindowPosition)
    } else {
        rememberWindowState(size = landWindowSize, position = defaultWindowPosition)
    }

    val standardChannel = remember { Channel<StandardAction>() }
    val standardFlow = remember(standardChannel) { standardChannel.consumeAsFlow() }
    val standardState = standardPresenter(standardFlow)

    val programmerChannel = remember { Channel<ProgrammerAction>() }
    val programmerFlow = remember(programmerChannel) { programmerChannel.consumeAsFlow() }
    val programmerState = programmerPresenter(programmerFlow)

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = Text.AppName,
        icon = painterResource("icon.png"),
        alwaysOnTop = Config.isFloat.value,
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
        CalculatorComposeTheme {
            val backgroundColor = MaterialTheme.colors.background

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = backgroundColor
            ) {
                HomeScreen(standardChannel, standardState, programmerChannel, programmerState)
            }
        }
    }
}

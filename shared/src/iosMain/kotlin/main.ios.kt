
import androidx.compose.ui.window.ComposeUIViewController
import com.equationl.common.platform.changeScreenOrientationFunc
import com.equationl.common.platform.vibrateFunc
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.KeyboardTypeProgrammer
import com.equationl.common.viewModel.KeyboardTypeStandard
import kotlinx.coroutines.channels.Channel

private var homeChannel: Channel<HomeAction>? = Channel()

fun MainViewController() = ComposeUIViewController {
    APP(
        homeChannelTop = homeChannel
    )
}


/**
 * @param orientation 0 竖，1 横
 * */
fun onScreenChange(orientation: Int) {
    if (orientation == 0) {
        homeChannel?.trySend(
            HomeAction.OnScreenOrientationChange(
                changeToType = KeyboardTypeStandard
            )
        )
    }
    else if (orientation == 1) {
        homeChannel?.trySend(
            HomeAction.OnScreenOrientationChange(
                changeToType = KeyboardTypeProgrammer
            )
        )
    }
}

fun changeScreenOrientation(callBack: (to: Int) -> Unit) {
    changeScreenOrientationFunc = callBack
}

fun setVibrateCallback(callBack: (type: Int) -> Unit) {
    vibrateFunc = callBack
}
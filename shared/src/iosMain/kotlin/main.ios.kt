
import androidx.compose.ui.window.ComposeUIViewController
import com.equationl.common.constant.KeyBoardTypeEnum
import com.equationl.common.platform.changeScreenOrientationFunc
import com.equationl.common.platform.vibrateFunc
import com.equationl.common.viewModel.HomeAction
import kotlinx.coroutines.channels.Channel

private var homeChannel: Channel<HomeAction>? = Channel(capacity = Channel.UNLIMITED)

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
                changeToType = KeyBoardTypeEnum.Standard
            )
        )
    }
    else if (orientation == 1) {
        homeChannel?.trySend(
            HomeAction.OnScreenOrientationChange(
                changeToType = KeyBoardTypeEnum.Programmer
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
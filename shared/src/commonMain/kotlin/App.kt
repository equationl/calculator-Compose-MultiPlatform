import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.equationl.common.constant.PlatformType
import com.equationl.common.platform.currentPlatform
import com.equationl.common.theme.CalculatorComposeTheme
import com.equationl.common.view.HomeScreen
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.homePresenter
import com.equationl.common.viewModel.programmerPresenter
import com.equationl.common.viewModel.standardPresenter
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.long_press_select_copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource


private val snackbarHostState =  SnackbarHostState()

private var isShowDialog by mutableStateOf(false)
private var dialogMsg by mutableStateOf("")
private var softwareKeyboardController: SoftwareKeyboardController? = null

fun showSnack(msg: String, isIndefinite: Boolean = false) {
    CoroutineScope(Dispatchers.Default).launch {
        showSnackSuspend(msg, isIndefinite)
    }
}

suspend fun showSnackSuspend(msg: String, isIndefinite: Boolean = false) {
    snackbarHostState.showSnackbar(
        message = msg,
        duration = if (isIndefinite) SnackbarDuration.Indefinite else SnackbarDuration.Short
    )
}

fun cancelSnack() {
    snackbarHostState.currentSnackbarData?.dismiss()
}

fun showDialog(
    msg: String
) {
    dialogMsg = msg
    isShowDialog = true
}

fun dismissDialog() {
    isShowDialog = false
}

fun hideKeyBoard() {
    softwareKeyboardController?.hide()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun APP(
    standardChannelTop: Channel<StandardAction>? = null,
    programmerChannelTop: Channel<ProgrammerAction>? = null,
    homeChannelTop: Channel<HomeAction>? = null,
    isFloat: Boolean? = null,
    boardType: Int? = null,
    onStart: (@Composable (backgroundColor: Color, isLight: Boolean) -> Unit)? = null
) {
    val homeChannel = homeChannelTop ?: remember { Channel(capacity = Channel.UNLIMITED) }
    val homeFlow = remember(homeChannel) { homeChannel.consumeAsFlow() }
    val homeState = homePresenter(homeFlow)


    val standardChannel = standardChannelTop ?: remember { Channel(capacity = Channel.UNLIMITED) }
    val standardFlow = remember(standardChannel) { standardChannel.consumeAsFlow() }
    val standardState = standardPresenter(standardFlow)

    val programmerChannel = programmerChannelTop ?: remember { Channel(capacity = Channel.UNLIMITED) }
    val programmerFlow = remember(programmerChannel) { programmerChannel.consumeAsFlow() }
    val programmerState = programmerPresenter(programmerFlow)

    softwareKeyboardController = LocalSoftwareKeyboardController.current

    standardChannel.trySend(StandardAction.Init(rememberCoroutineScope()))


    CalculatorComposeTheme {
        val backgroundColor = MaterialTheme.colors.background

        onStart?.invoke(backgroundColor, MaterialTheme.colors.isLight)

        LaunchedEffect(key1 = isFloat) {
            if (isFloat != null && boardType != null) {
                delay(50)
                homeChannel.trySend(HomeAction.InitState(isFloat, boardType))
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize().alpha(if (homeState.isFloat && currentPlatform() == PlatformType.Desktop) homeState.transparency else 1f),
            color = backgroundColor
        ) {
            Box(Modifier.fillMaxSize()) {
                HomeScreen(
                    homeChannel,
                    homeState,
                    standardChannel,
                    standardState,
                    programmerChannel,
                    programmerState
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                if (isShowDialog) {
                    Dialog(
                        onDismissRequest = {
                            isShowDialog = false
                        },
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(IntrinsicSize.Min)
                                //.heightIn(150.dp)
                                .background(MaterialTheme.colors.background)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(Res.string.long_press_select_copy), style = MaterialTheme.typography.h6)

                            Divider(modifier = Modifier.padding(8.dp))

                            SelectionContainer(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                Text(dialogMsg)
                            }

                        }
                    }
                }
            }
        }
    }
}
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.equationl.common.theme.CalculatorComposeTheme
import com.equationl.common.view.HomeScreen
import com.equationl.common.viewModel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


private val snackbarHostState =  SnackbarHostState()

private var isShowDialog by mutableStateOf(false)
private var dialogMsg by mutableStateOf("")

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

@Composable
fun APP(
    standardChannelTop: Channel<StandardAction>? = null,
    programmerChannelTop: Channel<ProgrammerAction>? = null,
    homeChannelTop: Channel<HomeAction>? = null,
    onStart: (@Composable (backgroundColor: Color, isLight: Boolean) -> Unit)? = null
) {
    val homeChannel = homeChannelTop ?: remember { Channel() }
    val homeFlow = remember(homeChannel) { homeChannel.consumeAsFlow() }
    val homeState = homePresenter(homeFlow)


    val standardChannel = standardChannelTop ?: remember { Channel() }
    val standardFlow = remember(standardChannel) { standardChannel.consumeAsFlow() }
    val standardState = standardPresenter(standardFlow)

    val programmerChannel = programmerChannelTop ?: remember { Channel() }
    val programmerFlow = remember(programmerChannel) { programmerChannel.consumeAsFlow() }
    val programmerState = programmerPresenter(programmerFlow)

    standardChannel.trySend(StandardAction.Init(rememberCoroutineScope()))

    CalculatorComposeTheme {
        val backgroundColor = MaterialTheme.colors.background

        onStart?.invoke(backgroundColor, MaterialTheme.colors.isLight)

        Surface(
            modifier = Modifier.fillMaxSize(),
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
                            Text("长按选择可复制", style = MaterialTheme.typography.h6)

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
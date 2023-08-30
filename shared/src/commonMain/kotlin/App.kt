import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.equationl.common.theme.CalculatorComposeTheme
import com.equationl.common.view.HomeScreen
import com.equationl.common.viewModel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


val snackbarHostState =  SnackbarHostState()

fun showSnack(msg: String) {
    CoroutineScope(Dispatchers.Default).launch {
        snackbarHostState.showSnackbar(msg)
    }
}

@Composable
fun APP(
    standardChannelTop: Channel<StandardAction>? = null,
    programmerChannelTop: Channel<ProgrammerAction>? = null,
    homeChannelTop: Channel<HomeAction>? = null,
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

    CalculatorComposeTheme {
        val backgroundColor = MaterialTheme.colors.background

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
            }
        }
    }
}
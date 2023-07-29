import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.equationl.common.theme.CalculatorComposeTheme
import com.equationl.common.view.HomeScreen
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.homePresenter
import com.equationl.common.viewModel.programmerPresenter
import com.equationl.common.viewModel.standardPresenter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun APP(
    standardChannelTop: Channel<StandardAction>? = null,
    programmerChannelTop: Channel<ProgrammerAction>? = null,
) {
    val homeChannel = remember { Channel<HomeAction>() }
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
            HomeScreen(
                homeChannel,
                homeState,
                standardChannel,
                standardState,
                programmerChannel,
                programmerState
            )
        }
    }
}
package com.equationl.common.overlay

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.equationl.common.theme.CalculatorComposeTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

@RequiresApi(Build.VERSION_CODES.R)
class OverlayService : ComposeOverlayViewService() {

    @Composable
    override fun Content() = OverlayDraggableContainer {
        val overLayChannel = remember { Channel<OverlayAction>() }
        val overLayFlow = remember(overLayChannel) { overLayChannel.consumeAsFlow() }
        val overLayState = overLayPresenter(overLayFlow)

        LaunchedEffect(Unit) {

            OverLayViewModel.viewEvents.collect {
                if (it is OverlayEvent.ChangeSize) {
                    updateSize(it.scale)
                }
            }
        }

        CalculatorComposeTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(overLayState.backgroundAlpha),
                color = MaterialTheme.colors.background.copy(alpha = overLayState.backgroundAlpha)
            ) {
                OverlayScreen(overLayChannel)
            }
        }
    }
}

package com.equationl.common.overlay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.blankj.utilcode.util.ActivityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private var viewScale: Float = 3f

private val _viewEvents = Channel<OverlayEvent>(Channel.BUFFERED)

object OverLayViewModel {
    val viewEvents = _viewEvents.receiveAsFlow()
}

@Composable
fun overLayPresenter(
    overLayActionFlow: Flow<OverlayAction>
): OverlayState {
    val overlayState = remember { mutableStateOf(OverlayState()) }

    LaunchedEffect(Unit) {
        overLayActionFlow.collect {action ->
            when (action) {
                is OverlayAction.ClickClose -> clickClose(action.context)
                is OverlayAction.ClickAdjustSize -> clickAdjustSize()
                is OverlayAction.ClickAdjustAlpha -> clickAdjustAlpha(overlayState)
                is OverlayAction.ClickBackFullScreen -> clickBackFullScreen(action.context)
            }
        }
    }

    return overlayState.value
}


private fun clickBackFullScreen(context: Context) {
    val launcherActivity = ActivityUtils.getLauncherActivity()
    val intent = Intent().apply {
        component = ComponentName(context.packageName, launcherActivity)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    context.startActivity(intent)
}

private fun clickAdjustAlpha(overlayState: MutableState<OverlayState>) {
    var alpha = overlayState.value.backgroundAlpha

    alpha += 0.2f

    if (alpha > 1f) {
        alpha = 0.2f
    }

    overlayState.value = overlayState.value.copy(backgroundAlpha = alpha)
}

private fun clickClose(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.stopService(Intent(context, OverlayService::class.java))
    }
}

private fun clickAdjustSize() {
    viewScale += 0.5f
    if (viewScale > 3) viewScale = 1f

    CoroutineScope(Dispatchers.Unconfined).launch {
        _viewEvents.send(OverlayEvent.ChangeSize(viewScale))
    }
}


data class OverlayState(
    val backgroundAlpha: Float = 1f
)

sealed class OverlayAction {
    object ClickAdjustSize: OverlayAction()
    object ClickAdjustAlpha: OverlayAction()
    data class ClickClose(val context: Context): OverlayAction()
    data class ClickBackFullScreen(val context: Context): OverlayAction()
}

sealed class OverlayEvent {
    data class ChangeSize(val scale: Float): OverlayEvent()
}

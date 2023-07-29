package com.equationl.common.overlay


// TODO 需要移植

/*import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.equationl.common.getScreenSize
import kotlin.math.roundToInt*/


/**
 * Service that is ready to display compose overlay view
 * @author Quentin Nivelais
 * @link https://gist.github.com/KONFeature/2f84436e1c0a1926505cac934d470f90
 *
 *  Edit by equationl (likehide.com)
 */

/*@RequiresApi(Build.VERSION_CODES.R)
abstract class ComposeOverlayViewService : ViewReadyService() {

    // Build the layout param for our popup
    private val layoutParams by lazy {
        WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            } else {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }

            val screenSize = getScreenSize(this@ComposeOverlayViewService)

            width = screenSize.x / 3
            height = screenSize.y / 3
        }
    }

    // The current offset of our overlay composable
    private var overlayOffset by mutableStateOf(Offset.Zero)

    // Access our window manager
    private val windowManager by lazy {
        overlayContext.getSystemService(WindowManager::class.java)
    }

    // Build our compose view
    private val composeView by lazy {
        ComposeView(overlayContext)
    }

    override fun onCreate() {
        super.onCreate()

        // Bound the compose lifecycle, view model and view tree saved state, into our view service
        ViewTreeLifecycleOwner.set(composeView, this)
        ViewTreeViewModelStoreOwner.set(composeView) { viewModelStore }
        composeView.setViewTreeSavedStateRegistryOwner(this)

        // Set the content of our compose view
        composeView.setContent { Content() }

        // Push the compose view into our window manager
        windowManager.addView(composeView, layoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove our compose view from the window manager
        windowManager.removeView(composeView)
    }

    @Composable
    abstract fun Content()

*//**
     * Draggable box container (not used by default, since not every overlay should be draggable)
     *//*
    @Composable
    internal fun OverlayDraggableContainer(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) =
        Box(
            modifier = modifier.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    // Update our current offset
                    val newOffset = overlayOffset + dragAmount
                    overlayOffset = newOffset

                    // Update the layout params, and then the view
                    layoutParams.apply {
                        x = overlayOffset.x.roundToInt()
                        y = overlayOffset.y.roundToInt()
                    }
                    windowManager.updateViewLayout(composeView, layoutParams)
                }
            },
            content = content
        )

    internal fun updateSize(scale: Float) {
        val screenSize = getScreenSize(this@ComposeOverlayViewService)
        layoutParams.apply {
            width = (screenSize.x / scale).roundToInt()
            height = (screenSize.y / scale).roundToInt()
        }
        windowManager.updateViewLayout(composeView, layoutParams)
    }
}*/

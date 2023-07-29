package com.equationl.common.overlay

// TODO 需要移植

/*import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner


@RequiresApi(Build.VERSION_CODES.R)
abstract class ViewReadyService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    private val savedStateRegistryController: SavedStateRegistryController by lazy(LazyThreadSafetyMode.NONE) {
        SavedStateRegistryController.create(this)
    }


    private val internalViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }


    internal val overlayContext: Context by lazy {
        // Get the default display
        val defaultDisplay: Display = getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
        // Create a display context, and then the window context
        createDisplayContext(defaultDisplay)
            .createWindowContext(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, null)
    }

    override fun onCreate() {
        super.onCreate()
        // Restore the last saved state registry
        savedStateRegistryController.performRestore(null)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun getViewModelStore(): ViewModelStore = internalViewModelStore
}*/

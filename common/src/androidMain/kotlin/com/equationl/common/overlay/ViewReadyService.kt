package com.equationl.common.overlay

import android.content.Context
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


/**
 * Service that is ready to display view, provide a ui context on the primary screen, and all the tools needed to built a view with state managment, view model etc
 * @author Quentin Nivelais
 * @link https://gist.github.com/KONFeature/2f84436e1c0a1926505cac934d470f90
 */

@RequiresApi(Build.VERSION_CODES.R)
abstract class ViewReadyService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {


/**
     * Build our saved state registry controller
     */

    private val savedStateRegistryController: SavedStateRegistryController by lazy(LazyThreadSafetyMode.NONE) {
        SavedStateRegistryController.create(this)
    }


/**
     * Build our view model store
     */

    private val internalViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }


/**
     * Context dedicated to the view
     */

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
}

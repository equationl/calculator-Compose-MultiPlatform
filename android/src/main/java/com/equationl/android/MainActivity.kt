package com.equationl.android

import APP
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import com.equationl.common.overlay.OverlayService
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.KeyboardTypeProgrammer
import com.equationl.common.viewModel.KeyboardTypeStandard
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {
    private var homeChannel: Channel<HomeAction>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            homeChannel = remember { Channel() }
            APP(
                homeChannelTop = homeChannel
            )
        }
    }

    override fun onResume() {
        super.onResume()

        // 每次打开主页都要把悬浮界面关闭
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopService(Intent(this, OverlayService::class.java))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            homeChannel?.trySend(
                HomeAction.OnScreenOrientationChange(
                    changeToType = KeyboardTypeProgrammer
                )
            )
        }
        else {
            homeChannel?.trySend(
                HomeAction.OnScreenOrientationChange(
                    changeToType = KeyboardTypeStandard
                )
            )
        }
    }
}
package com.equationl.android

import APP
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.equationl.common.overlay.OverlayService
import com.equationl.common.viewModel.HomeAction
import com.equationl.common.viewModel.KeyboardTypeProgrammer
import com.equationl.common.viewModel.KeyboardTypeStandard
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {
    private var homeChannel: Channel<HomeAction>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        // 暂时继续使用 systemUiController 实现沉浸式状态栏，等 activity 发布稳定版本了再迁移到 enableEdgeToEdge()
        // add on androidx.activity:activity:1.8.0
        // enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            homeChannel = remember { Channel() }

            val systemUiController = rememberSystemUiController()

            APP(
                homeChannelTop = homeChannel
            ) { backgroundColor, isLight ->
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = backgroundColor,
                        darkIcons = isLight
                    )
                }
            }
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
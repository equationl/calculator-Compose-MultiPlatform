package com.equationl.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.equationl.common.overlay.OverlayService
import com.equationl.common.theme.CalculatorComposeTheme
import com.equationl.common.view.HomeScreen
import com.equationl.common.viewModel.ProgrammerAction
import com.equationl.common.viewModel.StandardAction
import com.equationl.common.viewModel.programmerPresenter
import com.equationl.common.viewModel.standardPresenter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO 没有增加旋转监听

        setContent {
            val standardChannel = remember { Channel<StandardAction>() }
            val standardFlow = remember(standardChannel) { standardChannel.consumeAsFlow() }
            val standardState = standardPresenter(standardFlow)

            val programmerChannel = remember { Channel<ProgrammerAction>() }
            val programmerFlow = remember(programmerChannel) { programmerChannel.consumeAsFlow() }
            val programmerState = programmerPresenter(programmerFlow)

            CalculatorComposeTheme {
                val backgroundColor = MaterialTheme.colors.background

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = MaterialTheme.colors.isLight

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = backgroundColor,
                            darkIcons = useDarkIcons
                        )
                    }

                    HomeScreen(standardChannel, standardState, programmerChannel, programmerState)
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
}
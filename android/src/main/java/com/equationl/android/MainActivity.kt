package com.equationl.android

import android.content.Intent
import android.content.pm.ActivityInfo
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
import com.equationl.common.viewModel.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeChannel = Channel<HomeAction>()

        addOnConfigurationChangedListener {
            if (it.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                homeChannel.trySend(HomeAction.ClickMenu(changeToType = KeyboardTypeStandard, false))
            }
            else {
                homeChannel.trySend(HomeAction.ClickMenu(changeToType = KeyboardTypeProgrammer, false))
            }
        }

        setContent {

            val homeFlow = remember(homeChannel) { homeChannel.consumeAsFlow() }
            val homeState = homePresenter(homeFlow)


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

                    HomeScreen(homeChannel, homeState, standardChannel, standardState, programmerChannel, programmerState)
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
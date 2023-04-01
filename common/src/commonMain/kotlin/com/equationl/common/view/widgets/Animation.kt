package com.equationl.common.view.widgets

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun scrollToLeftAnimation(targetValue: Float = -5f): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val slipUpYAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        )
    )
    return slipUpYAnimation
}
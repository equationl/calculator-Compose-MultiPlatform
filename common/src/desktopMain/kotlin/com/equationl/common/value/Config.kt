package com.equationl.common.value

import androidx.compose.runtime.mutableStateOf
import com.equationl.common.viewModel.KeyboardTypeStandard

object Config {
    val boardType = mutableStateOf(KeyboardTypeStandard)
    val isFloat = mutableStateOf(false)
}
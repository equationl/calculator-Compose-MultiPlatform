package com.equationl.common.value

import androidx.compose.runtime.mutableStateOf
import com.equationl.common.constant.KeyBoardTypeEnum

object Config {
    val boardType = mutableStateOf(KeyBoardTypeEnum.Standard)
    val isFloat = mutableStateOf(false)
}
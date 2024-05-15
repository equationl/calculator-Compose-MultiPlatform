package com.equationl.common.constant

import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.app_name
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

object Text {
    @OptIn(ExperimentalResourceApi::class)
    val AppName: String
        get() {
            return runBlocking {
                getString(Res.string.app_name)
            }
        }
    /**
     * ASCII 非打印字符列表，这里按照索引转为名称缩写来显示
     * */
    val NonDisplayAscii = listOf(
        "[NUL]", "[SOH]", "[STX]", "[ETX]", "[EOT]", "[ENQ]", "[ACK]",
        "[BEL]", "[BS]",  "[TAB]", "[LF]",  "[VI]",  "[FF]",  "[CR]",
        "[SO]",  "[SI]",  "[DLE]", "[DC1]", "[DC2]", "[DC3]", "[DC4]",
        "[NAK]", "[SYN]", "[ETB]", "[CAN]", "[EM]",  "[SUB]",
        "[ESC]", "[FS]",  "[GS]",  "[RS]",  "[US]",
        )
}
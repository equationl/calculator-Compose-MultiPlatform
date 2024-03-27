package com.equationl.common.constant

object Text {
    const val AppName = "隐云计算器"

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
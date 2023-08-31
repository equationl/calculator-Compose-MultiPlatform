package com.equationl.common.utils

import kotlin.math.max

object LongUtil {
    val digits = charArrayOf(
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    )



    fun toBinaryString(i: Long): String {
        return toUnsignedString0(i, 1)
    }

    fun toHexString(i: Long): String {
        return toUnsignedString0(i, 4)
    }

    fun toOctalString(i: Long): String {
        return toUnsignedString0(i, 3)
    }

    fun toUnsignedString0(`val`: Long, shift: Int): String {
        // assert shift > 0 && shift <=5 : "Illegal shift value";
        val mag: Int = Long.SIZE_BITS - numberOfLeadingZeros(`val`)
        val chars: Int = max((mag + (shift - 1)) / shift, 1)
        //if (COMPACT_STRINGS) {
            val buf = ByteArray(chars)
            formatUnsignedLong0(`val`, shift, buf, 0, chars)
            return buf.map { it.toInt().toChar() }.toCharArray().concatToString()
//        } else {
//            val buf = ByteArray(chars * 2)
//            java.lang.Long.formatUnsignedLong0UTF16(`val`, shift, buf, 0, chars)
//            return String(buf, UTF16)
//        }
    }

    private fun formatUnsignedLong0(
        `val`: Long,
        shift: Int,
        buf: ByteArray,
        offset: Int,
        len: Int
    ) {
        var `val` = `val`
        var charPos = offset + len
        val radix = 1 shl shift
        val mask = radix - 1
        do {
            buf[--charPos] = digits[`val`.toInt() and mask].code.toByte()
            `val` = `val` ushr shift
        } while (charPos > offset)
    }

    fun numberOfLeadingZeros(i: Long): Int {
        val x = (i ushr 32).toInt()
        return if (x == 0) 32 + numberOfLeadingZeros(i.toInt()) else numberOfLeadingZeros(
            x
        )
    }

    fun numberOfLeadingZeros(i: Int): Int {
        // HD, Count leading 0's
        var i = i
        if (i <= 0) return if (i == 0) 32 else 0
        var n = 31
        if (i >= 1 shl 16) {
            n -= 16
            i = i ushr 16
        }
        if (i >= 1 shl 8) {
            n -= 8
            i = i ushr 8
        }
        if (i >= 1 shl 4) {
            n -= 4
            i = i ushr 4
        }
        if (i >= 1 shl 2) {
            n -= 2
            i = i ushr 2
        }
        return n - (i ushr 1)
    }
}
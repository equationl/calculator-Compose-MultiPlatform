package com.equationl.common.dataModel

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 数字按键
const val KeyIndex_0 = 0
const val KeyIndex_1 = 1
const val KeyIndex_2 = 2
const val KeyIndex_3 = 3
const val KeyIndex_4 = 4
const val KeyIndex_5 = 5
const val KeyIndex_6 = 6
const val KeyIndex_7 = 7
const val KeyIndex_8 = 8
const val KeyIndex_9 = 9
const val KeyIndex_A = 17  // 不按照顺序往下编号是因为在程序员键盘中使用的是 ascii 索引， 而数字 9 和 A 间隔了 7 位
const val KeyIndex_B = 18
const val KeyIndex_C = 19
const val KeyIndex_D = 20
const val KeyIndex_E = 21
const val KeyIndex_F = 22

// 运算按键
const val KeyIndex_Add = 100
const val KeyIndex_Minus = 101
const val KeyIndex_Multiply = 102
const val KeyIndex_Divide = 103
const val KeyIndex_NegativeNumber = 104
const val KeyIndex_Point = 105
const val KeyIndex_Reciprocal = 106
const val KeyIndex_Pow2 = 107
const val KeyIndex_Sqrt = 108
const val KeyIndex_Percentage = 109
const val KeyIndex_Lsh = 110
const val KeyIndex_Rsh = 111
const val KeyIndex_And = 112
const val KeyIndex_Or = 113
const val KeyIndex_Not = 114
const val KeyIndex_XOr = 117

// 功能按键
const val KeyIndex_Equal = 1000
const val KeyIndex_CE = 1001
const val KeyIndex_Clear = 1002
const val KeyIndex_Back = 1003


@Composable
fun numberColor(): Color = Color.Unspecified // MaterialTheme.colors.secondary

@Composable
fun functionColor(): Color = MaterialTheme.colors.primary

@Composable
fun equalColor(): Color = MaterialTheme.colors.primaryVariant

@Composable
fun standardKeyBoardBtn(): List<List<KeyBoardData>> = listOf(
        listOf(
            KeyBoardData("%", functionColor(),  KeyIndex_Percentage),
            KeyBoardData("CE", functionColor(), KeyIndex_CE),
            KeyBoardData("C", functionColor(),  KeyIndex_Clear),
            KeyBoardData("⇦", functionColor(),  KeyIndex_Back),
        ),
        listOf(
            KeyBoardData("1/x", functionColor(), KeyIndex_Reciprocal),
            KeyBoardData("x²", functionColor(), KeyIndex_Pow2),
            KeyBoardData("√x", functionColor(), KeyIndex_Sqrt),
            KeyBoardData(Operator.Divide.showText, functionColor(), KeyIndex_Divide),
        ),
        listOf(
            KeyBoardData("7", numberColor(), KeyIndex_7),
            KeyBoardData("8", numberColor(), KeyIndex_8),
            KeyBoardData("9", numberColor(), KeyIndex_9),
            KeyBoardData(Operator.MULTIPLY.showText, functionColor(), KeyIndex_Multiply),
        ),
        listOf(
            KeyBoardData("4", numberColor(), KeyIndex_4),
            KeyBoardData("5", numberColor(), KeyIndex_5),
            KeyBoardData("6", numberColor(), KeyIndex_6),
            KeyBoardData(Operator.MINUS.showText, functionColor(), KeyIndex_Minus),
        ),
        listOf(
            KeyBoardData("1", numberColor(), KeyIndex_1),
            KeyBoardData("2", numberColor(), KeyIndex_2),
            KeyBoardData("3", numberColor(), KeyIndex_3),
            KeyBoardData(Operator.ADD.showText, functionColor(), KeyIndex_Add),
        ),
        listOf(
            KeyBoardData("+/-", numberColor(), KeyIndex_NegativeNumber),
            KeyBoardData("0", numberColor(), KeyIndex_0),
            KeyBoardData(".", numberColor(), KeyIndex_Point),
            KeyBoardData("=", equalColor(), KeyIndex_Equal, isFilled = true),
        )
    )

@Composable
fun programmerNumberKeyBoardBtn(): List<List<KeyBoardData>> = listOf(
    listOf(
        KeyBoardData("D", numberColor(),  KeyIndex_D),
        KeyBoardData("E", numberColor(),  KeyIndex_E),
        KeyBoardData("F", numberColor(),  KeyIndex_F)
    ),
    listOf(
        KeyBoardData("A", numberColor(),  KeyIndex_A),
        KeyBoardData("B", numberColor(),  KeyIndex_B),
        KeyBoardData("C", numberColor(),  KeyIndex_C)
    ),
    listOf(
        KeyBoardData("7", numberColor(), KeyIndex_7),
        KeyBoardData("8", numberColor(),  KeyIndex_8),
        KeyBoardData("9", numberColor(),  KeyIndex_9)
    ),
    listOf(
        KeyBoardData("4", numberColor(), KeyIndex_4),
        KeyBoardData("5", numberColor(),  KeyIndex_5),
        KeyBoardData("6", numberColor(),  KeyIndex_6)
    ),
    listOf(
        KeyBoardData("1", numberColor(), KeyIndex_1),
        KeyBoardData("2", numberColor(),  KeyIndex_2),
        KeyBoardData("3", numberColor(),  KeyIndex_3)
    ),
    listOf(
        KeyBoardData("<<", functionColor(), KeyIndex_Lsh),
        KeyBoardData("0", numberColor(),  KeyIndex_0),
        KeyBoardData(">>", functionColor(),  KeyIndex_Rsh)
    )
)

@Composable
fun programmerFunctionKeyBoardBtn(): List<List<KeyBoardData>> = listOf(
    listOf(
        KeyBoardData("C", functionColor(),  KeyIndex_Clear),
        KeyBoardData("⇦", functionColor(),  KeyIndex_Back)
    ),
    listOf(
        KeyBoardData("CE", functionColor(),  KeyIndex_CE),
        KeyBoardData(Operator.Divide.showText, functionColor(),  KeyIndex_Divide)
    ),
    listOf(
        KeyBoardData("NOT", functionColor(),  KeyIndex_Not),
        KeyBoardData(Operator.MULTIPLY.showText, functionColor(),  KeyIndex_Multiply)
    ),
    listOf(
        KeyBoardData("XOR", functionColor(),  KeyIndex_XOr),
        KeyBoardData(Operator.MINUS.showText, functionColor(),  KeyIndex_Minus)
    ),
    listOf(
        KeyBoardData("AND", functionColor(), KeyIndex_And),
        KeyBoardData(Operator.ADD.showText, functionColor(),  KeyIndex_Add)
    ),
    listOf(
        KeyBoardData("OR", functionColor(),  KeyIndex_Or),
        KeyBoardData("=", equalColor(),  KeyIndex_Equal, isFilled = true)
    )
)

@Composable
fun overlayKeyBoardBtn(): List<List<KeyBoardData>> = listOf(
    listOf(
        KeyBoardData("CE", functionColor(), KeyIndex_CE),
        KeyBoardData("C", functionColor(),  KeyIndex_Clear),
        KeyBoardData("⇦", functionColor(),  KeyIndex_Back),
        KeyBoardData(Operator.Divide.showText, functionColor(), KeyIndex_Divide)
        ),
    listOf(
        KeyBoardData("7", numberColor(), KeyIndex_7),
        KeyBoardData("8", numberColor(), KeyIndex_8),
        KeyBoardData("9", numberColor(), KeyIndex_9),
        KeyBoardData(Operator.MULTIPLY.showText, functionColor(), KeyIndex_Multiply),
    ),
    listOf(
        KeyBoardData("4", numberColor(), KeyIndex_4),
        KeyBoardData("5", numberColor(), KeyIndex_5),
        KeyBoardData("6", numberColor(), KeyIndex_6),
        KeyBoardData(Operator.MINUS.showText, functionColor(), KeyIndex_Minus),
    ),
    listOf(
        KeyBoardData("1", numberColor(), KeyIndex_1),
        KeyBoardData("2", numberColor(), KeyIndex_2),
        KeyBoardData("3", numberColor(), KeyIndex_3),
        KeyBoardData(Operator.ADD.showText, functionColor(), KeyIndex_Add),
    ),
    listOf(
        KeyBoardData("±", numberColor(), KeyIndex_NegativeNumber),
        KeyBoardData("0", numberColor(), KeyIndex_0),
        KeyBoardData(".", numberColor(), KeyIndex_Point),
        KeyBoardData("=", equalColor(), KeyIndex_Equal, isFilled = true),
    )
)

val BitOperationList = listOf(
    Operator.NOT,
    Operator.AND,
    Operator.OR,
    Operator.XOR,
    Operator.LSH,
    Operator.RSH
)

val asciiForbidBtn = listOf(
    KeyIndex_Add,
    KeyIndex_Minus,
    KeyIndex_Multiply,
    KeyIndex_Divide,
    KeyIndex_Lsh,
    KeyIndex_Rsh,
    KeyIndex_And,
    KeyIndex_Or,
    KeyIndex_Not,
    KeyIndex_XOr,
    KeyIndex_Equal,
)

data class KeyBoardData(
    val text: String,
    /**
     * 设置按钮颜色，设置范围取决于 [isFilled]
     * */
    val background: Color,
    val index: Int,
    /**
     * 是否填充该按钮，如果为 true 则 [background] 用于填充该按钮背景；否则，[background] 用于设置该按钮字体颜色
     * */
    val isFilled: Boolean = false,
    val isAvailable: Boolean = true
)

enum class Operator(val showText: String) {
    ADD("+"),
    MINUS("-"),
    MULTIPLY("×"),
    Divide("÷"),
    SQRT("√"),
    POW2("²"),
    NOT("NOT"),
    AND(" AND "),
    OR(" OR "),
    XOR(" XOR "),
    LSH(" Lsh "),
    RSH(" Rsh "),
    NUll("")
}

enum class InputBase(val number: Int, val forbidBtn: List<Int>) {
    HEX(16, listOf()),
    DEC(10, listOf(
        KeyIndex_A,
        KeyIndex_B,
        KeyIndex_C,
        KeyIndex_D,
        KeyIndex_E,
        KeyIndex_F
    )),
    OCT(8, listOf(
        KeyIndex_A,
        KeyIndex_B,
        KeyIndex_C,
        KeyIndex_D,
        KeyIndex_E,
        KeyIndex_F,
        KeyIndex_8,
        KeyIndex_9,
    )),
    BIN(2, listOf(
        KeyIndex_A,
        KeyIndex_B,
        KeyIndex_C,
        KeyIndex_D,
        KeyIndex_E,
        KeyIndex_F,
        KeyIndex_9,
        KeyIndex_8,
        KeyIndex_7,
        KeyIndex_6,
        KeyIndex_5,
        KeyIndex_4,
        KeyIndex_3,
        KeyIndex_2
    ))
}
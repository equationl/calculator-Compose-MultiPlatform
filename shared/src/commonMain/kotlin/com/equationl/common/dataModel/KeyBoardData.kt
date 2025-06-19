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
const val KeyIndex_Pow3 = 118
const val KeyIndex_Sqrt3 = 119
const val KeyIndex_Abs = 120
const val KeyIndex_Exp = 121
const val KeyIndex_Mod = 122
const val KeyIndex_LeftBrackets = 123
const val KeyIndex_RightBrackets = 124
const val KeyIndex_Factorial = 125
const val KeyIndex_XPowY = 126
const val KeyIndex_XSqrtY = 127
const val KeyIndex_10PowX = 128
const val KeyIndex_2PowX = 129
const val KeyIndex_Log = 130
const val KeyIndex_LogYX = 131
const val KeyIndex_Ln = 132
const val KeyIndex_EPowX = 133

// 常量
const val KeyIndex_Constant_PI = 200
const val KeyIndex_Constant_E = 201

// 操作按键
const val KeyIndex_Equal = 1000
const val KeyIndex_CE = 1001
const val KeyIndex_Clear = 1002
const val KeyIndex_Back = 1003
const val KeyIndex_CE_Clear = 1004

// 记忆按键
const val KeyIndex_MemoryClear = 1104
const val KeyIndex_MemoryRead = 1105
const val KeyIndex_MemoryPlus = 1106
const val KeyIndex_MemoryMinus = 1107
const val KeyIndex_MemorySave = 1108
const val KeyIndex_MemoryList = 1109

// 三角函数按键
const val KeyIndex_Sin = 2000
const val KeyIndex_Cos = 2001
const val KeyIndex_Tan = 2002
const val KeyIndex_Sec = 2003
const val KeyIndex_Csc = 2004
const val KeyIndex_Cot = 2005
const val KeyIndex_ArcSin = 2006
const val KeyIndex_ArcCos = 2007
const val KeyIndex_ArcTan = 2008
const val KeyIndex_ArcSec = 2009
const val KeyIndex_ArcCsc = 2010
const val KeyIndex_ArcCot = 2011
const val KeyIndex_SinH = 2012
const val KeyIndex_CosH = 2013
const val KeyIndex_TanH = 2014
const val KeyIndex_SecH = 2015
const val KeyIndex_CscH = 2016
const val KeyIndex_CotH = 2017
const val KeyIndex_ArcSinH = 2018
const val KeyIndex_ArcCosH = 2019
const val KeyIndex_ArcTanH = 2020
const val KeyIndex_ArcSecH = 2021
const val KeyIndex_ArcCscH = 2022
const val KeyIndex_ArcCotH = 2023


// 其他按键
/** 切换角度、弧度、梯度*/
const val KeyIndex_ToggleAngle = 3000
/** 切换数字显示方式，科学计数法*/
const val KeyIndex_ToggleResultType = 3001
const val KeyIndex_Floor = 3002
const val KeyIndex_Ceil = 3003
const val KeyIndex_Dms = 3004
const val KeyIndex_Deg = 3005
const val KeyIndex_Random = 3006


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
fun scienceKeyBoardBtn(angleType: Int = 0, resultType: Int = 0, clearType: Int = 0): List<List<KeyBoardData>> = listOf(
    listOf(
        KeyBoardData(ScienceOperator.ToggleAngle.showTextGroup.getOrNull(angleType) ?: ScienceOperator.ToggleAngle.showText, functionColor(),  KeyIndex_ToggleAngle),
        KeyBoardData(ScienceOperator.ToggleResultType.showText, functionColor(), KeyIndex_ToggleResultType, isFilled = resultType == 1),
        KeyBoardData(ScienceOperator.PI.showText, functionColor(),  KeyIndex_Constant_PI),
        KeyBoardData(ScienceOperator.E.showText, functionColor(),  KeyIndex_Constant_E),
        KeyBoardData(if (clearType == 0) "C" else "CE", functionColor(),  KeyIndex_CE_Clear),
        KeyBoardData("⇦", functionColor(),  KeyIndex_Back),
    ),
    listOf(
        KeyBoardData(ScienceOperator.Pow2.showText, functionColor(), KeyIndex_Pow2),
        KeyBoardData(ScienceOperator.Pow3.showText, functionColor(), KeyIndex_Pow3),
        KeyBoardData(ScienceOperator.Reciprocal.showText, functionColor(), KeyIndex_Reciprocal),
        KeyBoardData(ScienceOperator.Abs.showText, functionColor(), KeyIndex_Abs),
        KeyBoardData(ScienceOperator.Exp.showText, functionColor(), KeyIndex_Exp),
        KeyBoardData(ScienceOperator.Mod.showText, functionColor(), KeyIndex_Mod),
    ),
    listOf(
        KeyBoardData(ScienceOperator.Sqrt.showText, functionColor(), KeyIndex_Sqrt),
        KeyBoardData(ScienceOperator.Sqrt3.showText, functionColor(), KeyIndex_Sqrt3),
        KeyBoardData(ScienceOperator.LeftBrackets.showText, functionColor(), KeyIndex_LeftBrackets),
        KeyBoardData(ScienceOperator.RightBrackets.showText, functionColor(), KeyIndex_RightBrackets),
        KeyBoardData(ScienceOperator.Factorial.showText, functionColor(), KeyIndex_Factorial),
        KeyBoardData(ScienceOperator.Divide.showText, functionColor(), KeyIndex_Divide),
    ),
    listOf(
        KeyBoardData(ScienceOperator.XPowY.showText, functionColor(), KeyIndex_XPowY),
        KeyBoardData(ScienceOperator.XSqrtY.showText, functionColor(), KeyIndex_XSqrtY),
        KeyBoardData("7", numberColor(), KeyIndex_7),
        KeyBoardData("8", numberColor(), KeyIndex_8),
        KeyBoardData("9", numberColor(), KeyIndex_9),
        KeyBoardData(ScienceOperator.MULTIPLY.showText, functionColor(), KeyIndex_Multiply),
    ),
    listOf(
        KeyBoardData(ScienceOperator.Op10PowX.showText, functionColor(), KeyIndex_10PowX),
        KeyBoardData(ScienceOperator.Op2PowX.showText, functionColor(), KeyIndex_2PowX),
        KeyBoardData("4", numberColor(), KeyIndex_4),
        KeyBoardData("5", numberColor(), KeyIndex_5),
        KeyBoardData("6", numberColor(), KeyIndex_6),
        KeyBoardData(ScienceOperator.MINUS.showText, functionColor(), KeyIndex_Minus),
    ),
    listOf(
        KeyBoardData(ScienceOperator.Log.showText, functionColor(), KeyIndex_Log),
        KeyBoardData(ScienceOperator.LogYX.showText, functionColor(), KeyIndex_LogYX),
        KeyBoardData("1", numberColor(), KeyIndex_1),
        KeyBoardData("2", numberColor(), KeyIndex_2),
        KeyBoardData("3", numberColor(), KeyIndex_3),
        KeyBoardData(ScienceOperator.ADD.showText, functionColor(), KeyIndex_Add),
    ),
    listOf(
        KeyBoardData(ScienceOperator.Ln.showText, functionColor(), KeyIndex_Ln),
        KeyBoardData(ScienceOperator.EPowX.showText, functionColor(), KeyIndex_EPowX),
        KeyBoardData(ScienceOperator.NegativeNumber.showText, functionColor(), KeyIndex_NegativeNumber),
        KeyBoardData("0", numberColor(), KeyIndex_0,),
        KeyBoardData(".", functionColor(), KeyIndex_Point),
        KeyBoardData("=", equalColor(), KeyIndex_Equal, isFilled = true),
    )
)

@Composable
fun scienceTrigonometricFunctionKeyBoardBtn(): List<List<KeyBoardData>> = listOf(
    listOf(
        KeyBoardData(ScienceOperator.Sin.showText, functionColor(),  KeyIndex_Sin),
        KeyBoardData(ScienceOperator.Cos.showText, functionColor(),  KeyIndex_Cos),
        KeyBoardData(ScienceOperator.Tan.showText, functionColor(),  KeyIndex_Tan),
        KeyBoardData(ScienceOperator.Sec.showText, functionColor(),  KeyIndex_Sec),
        KeyBoardData(ScienceOperator.Csc.showText, functionColor(),  KeyIndex_Csc),
        KeyBoardData(ScienceOperator.Cot.showText, functionColor(),  KeyIndex_Cot),
    ),
    listOf(
        KeyBoardData(ScienceOperator.SinH.showText, functionColor(),  KeyIndex_SinH, isFilled = true),
        KeyBoardData(ScienceOperator.CosH.showText, functionColor(),  KeyIndex_CosH, isFilled = true),
        KeyBoardData(ScienceOperator.TanH.showText, functionColor(),  KeyIndex_TanH, isFilled = true),
        KeyBoardData(ScienceOperator.SecH.showText, functionColor(),  KeyIndex_SecH, isFilled = true),
        KeyBoardData(ScienceOperator.CscH.showText, functionColor(),  KeyIndex_CscH, isFilled = true),
        KeyBoardData(ScienceOperator.CotH.showText, functionColor(),  KeyIndex_CotH, isFilled = true),
    ),
    listOf(
        KeyBoardData(ScienceOperator.ArcSin.showText, functionColor(),  KeyIndex_ArcSin),
        KeyBoardData(ScienceOperator.ArcCos.showText, functionColor(),  KeyIndex_ArcCos),
        KeyBoardData(ScienceOperator.ArcTan.showText, functionColor(),  KeyIndex_ArcTan),
        KeyBoardData(ScienceOperator.ArcSec.showText, functionColor(),  KeyIndex_ArcSec),
        KeyBoardData(ScienceOperator.ArcCsc.showText, functionColor(),  KeyIndex_ArcCsc),
        KeyBoardData(ScienceOperator.ArcCot.showText, functionColor(),  KeyIndex_ArcCot),
    ),
    listOf(
        KeyBoardData(ScienceOperator.ArcSinH.showText, functionColor(),  KeyIndex_ArcSinH, isFilled = true),
        KeyBoardData(ScienceOperator.ArcCosH.showText, functionColor(),  KeyIndex_ArcCosH, isFilled = true),
        KeyBoardData(ScienceOperator.ArcTanH.showText, functionColor(),  KeyIndex_ArcTanH, isFilled = true),
        KeyBoardData(ScienceOperator.ArcSecH.showText, functionColor(),  KeyIndex_ArcSecH, isFilled = true),
        KeyBoardData(ScienceOperator.ArcCscH.showText, functionColor(),  KeyIndex_ArcCscH, isFilled = true),
        KeyBoardData(ScienceOperator.ArcCotH.showText, functionColor(),  KeyIndex_ArcCotH, isFilled = true),
    )
)

@Composable
fun scienceOtherFunctionKeyBoardBtn(): List<List<KeyBoardData>> = listOf(
    listOf(
        KeyBoardData(ScienceOperator.Abs.showText, functionColor(), KeyIndex_Abs),
        KeyBoardData(ScienceOperator.Floor.showText, functionColor(), KeyIndex_Floor),
        KeyBoardData(ScienceOperator.Ceil.showText, functionColor(), KeyIndex_Ceil),
    ),
    listOf(
        KeyBoardData(ScienceOperator.Random.showText, functionColor(), KeyIndex_Random),
        KeyBoardData(ScienceOperator.Dms.showText, functionColor(), KeyIndex_Dms),
        KeyBoardData(ScienceOperator.DEG.showText, functionColor(), KeyIndex_Deg),
    ),
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

@Composable
fun memoryFunctionKeyBoardBtn(): List<KeyBoardData> = listOf(
    KeyBoardData("MC", numberColor(),  KeyIndex_MemoryClear),
    KeyBoardData("MR", numberColor(),  KeyIndex_MemoryRead),
    KeyBoardData("M+", numberColor(),  KeyIndex_MemoryPlus),
    KeyBoardData("M-", numberColor(),  KeyIndex_MemoryMinus),
    KeyBoardData("MS", numberColor(),  KeyIndex_MemorySave),
    KeyBoardData("ML", numberColor(),  KeyIndex_MemoryList)
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

val memoryForbidBtnOnNoData = listOf(
    KeyIndex_MemoryClear,
    KeyIndex_MemoryRead,
    KeyIndex_MemoryList
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

enum class ScienceOperator(val showText: String, vararg val showTextGroup: String = arrayOf()) {
    ADD("+"),
    MINUS("-"),
    MULTIPLY("×"),
    Divide("÷"),
    ToggleAngle("DEG", "DEG", "RAD", "GRAD"),
    ToggleResultType("F-E"),
    PI("π"),
    E("e"),
    Pow2("x²"),
    Pow3("x³"),
    Reciprocal("1/x"),
    Abs("|x|"),
    Exp("exp"),
    Mod("Mod"),
    Sqrt("²√x"),
    Sqrt3("³√x"),
    LeftBrackets("("),
    RightBrackets(")"),
    Factorial("n!"),
    XPowY("xʸ"),
    XSqrtY("ʸ√x"),
    Op10PowX("10ˣ"),
    Op2PowX("2ˣ"),
    Log("log"),
    LogYX("logᵧx"),
    Ln("ln"),
    EPowX("eˣ"),
    NegativeNumber("±"),
    Sin("sin"),
    Cos("cos"),
    Tan("tan"),
    Sec("sec"),
    Csc("csc"),
    Cot("cot"),
    ArcSin("sin⁻¹"),
    ArcCos("cos⁻¹"),
    ArcTan("tan⁻¹"),
    ArcSec("sec⁻¹"),
    ArcCsc("csc⁻¹"),
    ArcCot("cot⁻¹"),
    SinH("sinh"),
    CosH("cosh"),
    TanH("tanh"),
    SecH("sech"),
    CscH("csch"),
    CotH("coth"),
    ArcSinH("sinh⁻¹"),
    ArcCosH("cosh⁻¹"),
    ArcTanH("tanh⁻¹"),
    ArcSecH("sech⁻¹"),
    ArcCscH("csch⁻¹"),
    ArcCotH("coth⁻¹"),
    Floor("⌊x⌋"),
    Ceil("⌈x⌉"),
    Random("Rand"),
    Dms("→DMS"),
    DEG("→DEG"),
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
package com.equationl.common.view.widgets

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * @author Róbert Nagy
 *
 * @link https://stackoverflow.com/a/69735469
 *
 *  Edit by equationl (http://likehide.com)
 *
 * */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    minSize: TextUnit = 12.sp
) {
    BoxWithConstraints {
        var shrunkFontSize = fontSize

        if (shrunkFontSize >= minSize) {
            val calculateIntrinsics = @Composable {
                ParagraphIntrinsics(
                    text, TextStyle(
                        color = color,
                        fontSize = shrunkFontSize,
                        fontWeight = fontWeight,
                        textAlign = textAlign,
                        lineHeight = lineHeight,
                        fontFamily = fontFamily,
                        textDecoration = textDecoration,
                        fontStyle = fontStyle,
                        letterSpacing = letterSpacing
                    ),
                    listOf(), listOf(), LocalDensity.current,
                    LocalFontFamilyResolver.current
                )
            }

            var intrinsics = calculateIntrinsics()
            with(LocalDensity.current) {
                while (intrinsics.maxIntrinsicWidth > maxWidth.toPx()) {
                    shrunkFontSize *= 0.9
                    if (shrunkFontSize < minSize) {
                        shrunkFontSize = minSize
                        break
                    }
                    intrinsics = calculateIntrinsics()
                }
            }
        }
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = shrunkFontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            onTextLayout = onTextLayout,
            style = style,
            maxLines = 1,
            overflow = if (shrunkFontSize <= minSize) TextOverflow.Ellipsis else TextOverflow.Clip
        )
    }
}
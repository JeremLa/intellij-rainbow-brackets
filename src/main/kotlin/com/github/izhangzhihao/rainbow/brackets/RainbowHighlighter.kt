package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

object RainbowHighlighter {

    val roundBrackets: CharArray = charArrayOf('(', ')')
    val squareBrackets: CharArray = charArrayOf('[', ']')
    val squigglyBrackets: CharArray = charArrayOf('{', '}')
    val angleBrackets: CharArray = charArrayOf('<', '>')

    val roundBracketsColors: Array<Color> by lazy { jBColor(settings.lightRoundBracketsColors, settings.darkRoundBracketsColors) }

    val squareBracketsColors: Array<Color> by lazy { jBColor(settings.lightSquareBracketsColors, settings.darkSquareBracketsColors) }

    val squigglyBracketsColors: Array<Color> by lazy { jBColor(settings.lightSquigglyBracketsColors, settings.darkSquigglyBracketsColors) }

    val angleBracketsColor: Array<Color> by lazy { jBColor(settings.lightAngleBracketsColor, settings.darkAngleBracketsColor) }

    private val settings = RainbowSettings.instance

    val isRainbowEnabled get() = settings.isRainbowEnabled
    val isEnableRainbowRoundBrackets get() = settings.isEnableRainbowRoundBrackets
    val isEnableRainbowSquigglyBrackets get() = settings.isEnableRainbowSquigglyBrackets
    val isEnableRainbowSquareBrackets get() = settings.isEnableRainbowSquareBrackets
    val isEnableRainbowAngleBrackets get() = settings.isEnableRainbowAngleBrackets
    val isDoNOTRainbowifyBracketsWithoutContent get() = settings.isDoNOTRainbowifyBracketsWithoutContent

    private val rainbowElement: HighlightInfoType = HighlightInfoType
            .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    fun Array<Color>.getColor(level: Int) = this[level % size]

    private val PsiElement.isRoundBracket get() = roundBrackets.any { textContains(it) }
    private val PsiElement.isSquareBracket get() = squareBrackets.any { textContains(it) }
    private val PsiElement.isSquigglyBracket get() = squigglyBrackets.any { textContains(it) }
    private val PsiElement.isAngleBracket get() = angleBrackets.any { textContains(it) }

    private fun createTextAttributes(element: PsiElement, level: Int): TextAttributes? {
        if (!isRainbowEnabled) {
            return null
        }

        val color = when {
            element.isRoundBracket -> if (isEnableRainbowRoundBrackets) roundBracketsColors else return null
            element.isSquareBracket -> if (isEnableRainbowSquareBrackets) squareBracketsColors else return null
            element.isSquigglyBracket -> if (isEnableRainbowSquigglyBrackets) squigglyBracketsColors else return null
            element.isAngleBracket -> if (isEnableRainbowAngleBrackets) angleBracketsColor else return null
            else -> roundBracketsColors
        }.getColor(level)

        return TextAttributes(color, null, null, null, Font.PLAIN)
    }

    fun getHighlightInfo(element: PsiElement, level: Int)
            : HighlightInfo? = createTextAttributes(element, level)
            ?.let {
                HighlightInfo
                        .newHighlightInfo(rainbowElement)
                        .textAttributes(it)
                        .range(element)
                        .create()
            }

    private fun jBColor(light: Array<String>, dark: Array<String>): Array<Color> =
            light.zip(dark).map { JBColor(Integer.decode(it.first), Integer.decode(it.second)) }.toTypedArray()
}

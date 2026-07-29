package com.mimika.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Colors lifted 1:1 from the concept prototype's CSS custom properties
 * (mimika-unified-concept.html :root block).
 */
object MimikaColors {
    val bg = Color(0xFF060607)
    val text = Color(0xFFF5F5F7)
    val muted = Color(0xFF7A7A82)
    val mutedDim = Color(0xFF4A4A50)
    val hairline = Color(0x0FFFFFFF) // rgba(255,255,255,0.06)
    val accent = Color(0xFF4C8DFF)
    val accentSoft = Color(0x294C8DFF) // rgba(76,141,255,0.16)
    val squareOff = Color(0x0DFFFFFF) // rgba(255,255,255,0.05)
    val squareBorder = Color(0x12FFFFFF) // rgba(255,255,255,0.07)
}

object MimikaType {
    val headlineLarge = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp)
    val headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp)
    val label = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
    val sectionLabel = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
    val body = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    val bodyMedium = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
    val caption = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val small = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val tiny = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
}

class MimikaColorsHolder {
    val bg = MimikaColors.bg
    val text = MimikaColors.text
    val muted = MimikaColors.muted
    val mutedDim = MimikaColors.mutedDim
    val hairline = MimikaColors.hairline
    val accent = MimikaColors.accent
    val accentSoft = MimikaColors.accentSoft
    val squareOff = MimikaColors.squareOff
    val squareBorder = MimikaColors.squareBorder
}

class MimikaTypeHolder {
    val headlineLarge = MimikaType.headlineLarge
    val headlineMedium = MimikaType.headlineMedium
    val label = MimikaType.label
    val sectionLabel = MimikaType.sectionLabel
    val body = MimikaType.body
    val bodyMedium = MimikaType.bodyMedium
    val caption = MimikaType.caption
    val small = MimikaType.small
    val tiny = MimikaType.tiny
}

private val LocalMimikaColors = staticCompositionLocalOf { MimikaColorsHolder() }
private val LocalMimikaType = staticCompositionLocalOf { MimikaTypeHolder() }

@Composable
fun MimikaTheme(content: @Composable () -> Unit) {
    content()
}

/** Short accessor mirroring the `Letify.colors` / `Letify.typography` pattern. */
object Mimika {
    val colors: MimikaColorsHolder
        @Composable get() = LocalMimikaColors.current
    val typography: MimikaTypeHolder
        @Composable get() = LocalMimikaType.current
}

val Unspecified: TextUnit = TextUnit.Unspecified

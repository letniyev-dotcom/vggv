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
    val bg = Color(0xFF0A0A0B)
    val text = Color(0xFFF5F5F7)
    val muted = Color(0xFF8B8B93)
    val mutedDim = Color(0xFF55555C)
    val hairline = Color(0x12FFFFFF) // rgba(255,255,255,0.07)
    val accent = Color(0xFF4C8DFF)
    val accentSoft = Color(0x294C8DFF) // rgba(76,141,255,0.16)
    val accentDim = Color(0x1A4C8DFF) // rgba(76,141,255,0.10)
    val squareOff = Color(0x0DFFFFFF) // rgba(255,255,255,0.05)
    val squareBorder = Color(0x12FFFFFF) // rgba(255,255,255,0.07)
    // Rounded-card surfaces — used ONLY for the current-task card on the
    // Plan page and the habit cards on the Habits page. Every other
    // surface in the app stays flat with hairline separators.
    val cardBg = Color(0xFF17171A)
    val cardBgSoft = Color(0x09FFFFFF) // rgba(255,255,255,0.035)
    val cardBorder = Color(0x12FFFFFF) // rgba(255,255,255,0.07)
}

/**
 * Type scale bumped up ~10-15% from the first pass. The original sizes
 * matched the HTML concept's CSS px 1:1, but that concept was viewed as a
 * 320px-wide mock zoomed up on a desktop monitor — on a real 390-430dp
 * phone the same absolute sp values read noticeably smaller. This scale
 * is tuned for how it actually looks on-device.
 */
object MimikaType {
    val headlineLarge = TextStyle(fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.9).sp)
    val headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp)
    val cardTitle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp)
    val label = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
    val sectionLabel = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
    val body = TextStyle(fontSize = 15.5.sp, fontWeight = FontWeight.SemiBold)
    val bodyMedium = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Medium)
    val caption = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
    val small = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
    val tiny = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
    val tab = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.1).sp)
}

class MimikaColorsHolder {
    val bg = MimikaColors.bg
    val text = MimikaColors.text
    val muted = MimikaColors.muted
    val mutedDim = MimikaColors.mutedDim
    val hairline = MimikaColors.hairline
    val accent = MimikaColors.accent
    val accentSoft = MimikaColors.accentSoft
    val accentDim = MimikaColors.accentDim
    val squareOff = MimikaColors.squareOff
    val squareBorder = MimikaColors.squareBorder
    val cardBg = MimikaColors.cardBg
    val cardBgSoft = MimikaColors.cardBgSoft
    val cardBorder = MimikaColors.cardBorder
}

class MimikaTypeHolder {
    val headlineLarge = MimikaType.headlineLarge
    val headlineMedium = MimikaType.headlineMedium
    val cardTitle = MimikaType.cardTitle
    val label = MimikaType.label
    val sectionLabel = MimikaType.sectionLabel
    val body = MimikaType.body
    val bodyMedium = MimikaType.bodyMedium
    val caption = MimikaType.caption
    val small = MimikaType.small
    val tiny = MimikaType.tiny
    val tab = MimikaType.tab
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

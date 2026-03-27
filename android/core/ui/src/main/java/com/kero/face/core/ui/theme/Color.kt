package com.kero.face.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Primary palette ──
val PeachPink = Color(0xFFFF8FAB)
val SoftCoral = Color(0xFFFFB3C1)
val WarmCream = Color(0xFFFFF5E4)
val LightCream = Color(0xFFFFFAF3)

// ── Secondary palette ──
val SkyLavender = Color(0xFFC8B6FF)
val MintGreen = Color(0xFFB8F3CE)
val MintGreenDark = Color(0xFF5DBF80)
val PuppyBrown = Color(0xFF8B6F47)
val DarkCocoa = Color(0xFF3D2C2E)
val WarmGray = Color(0xFF9E8E8E)

// ── Semantic colors ──
val ErrorRed = Color(0xFFFF5C7A)
val ErrorRedLight = Color(0xFFFFE0E6)
val InfoBlue = Color(0xFF74B9FF)
val InfoBlueLight = Color(0xFFD6EEFF)
val WarningYellow = Color(0xFFFFD166)
val WarningYellowLight = Color(0xFFFFF3CD)

// ── Utility ──
val BorderColor = Color(0xFFF0E6D8)
val OverlayColor = Color(0x663D2C2E)
val PureWhite = Color(0xFFFFFFFF)

@Immutable
data class FaceChangerColors(
    val primary: Color = PeachPink,
    val primaryContainer: Color = SoftCoral,
    val secondary: Color = SkyLavender,
    val tertiary: Color = MintGreen,
    val tertiaryDark: Color = MintGreenDark,
    val accent: Color = PuppyBrown,
    val background: Color = LightCream,
    val surface: Color = PureWhite,
    val surfaceVariant: Color = WarmCream,
    val onPrimary: Color = PureWhite,
    val onBackground: Color = DarkCocoa,
    val onSurface: Color = DarkCocoa,
    val onSurfaceVariant: Color = WarmGray,
    val border: Color = BorderColor,
    val overlay: Color = OverlayColor,
    val error: Color = ErrorRed,
    val errorContainer: Color = ErrorRedLight,
    val success: Color = MintGreen,
    val successDark: Color = MintGreenDark,
    val info: Color = InfoBlue,
    val infoContainer: Color = InfoBlueLight,
    val warning: Color = WarningYellow,
    val warningContainer: Color = WarningYellowLight,
)

val LocalFaceChangerColors = staticCompositionLocalOf { FaceChangerColors() }

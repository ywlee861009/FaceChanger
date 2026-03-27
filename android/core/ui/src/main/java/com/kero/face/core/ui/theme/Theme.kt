package com.kero.face.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val FaceChangerColorScheme = lightColorScheme(
    primary = PeachPink,
    onPrimary = PureWhite,
    primaryContainer = SoftCoral,
    onPrimaryContainer = DarkCocoa,
    secondary = SkyLavender,
    onSecondary = PureWhite,
    secondaryContainer = SkyLavender,
    onSecondaryContainer = DarkCocoa,
    tertiary = MintGreen,
    onTertiary = DarkCocoa,
    tertiaryContainer = MintGreen,
    onTertiaryContainer = DarkCocoa,
    background = LightCream,
    onBackground = DarkCocoa,
    surface = PureWhite,
    onSurface = DarkCocoa,
    surfaceVariant = WarmCream,
    onSurfaceVariant = WarmGray,
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed,
    outline = BorderColor,
    outlineVariant = BorderColor,
)

@Composable
fun FaceChangerTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFaceChangerColors provides FaceChangerColors(),
        LocalFaceChangerSpacing provides FaceChangerSpacing(),
        LocalFaceChangerShapes provides FaceChangerShapes(),
    ) {
        MaterialTheme(
            colorScheme = FaceChangerColorScheme,
            typography = Typography,
            content = content,
        )
    }
}

object FcTheme {
    val colors: FaceChangerColors
        @Composable
        @ReadOnlyComposable
        get() = LocalFaceChangerColors.current

    val spacing: FaceChangerSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalFaceChangerSpacing.current

    val shapes: FaceChangerShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalFaceChangerShapes.current
}

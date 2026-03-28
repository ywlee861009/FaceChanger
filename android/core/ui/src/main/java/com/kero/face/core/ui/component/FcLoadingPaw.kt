package com.kero.face.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

/**
 * Animated paw print loading indicator with white paws.
 * Use on colored backgrounds (e.g. inside a primary button).
 */
@Composable
fun FcLoadingPaw(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    PawLoadingRow(modifier = modifier, color = color)
}

/**
 * Animated paw print loading indicator with primary-colored paws.
 * Use on light/white backgrounds.
 */
@Composable
fun FcLoadingPawAlt(
    modifier: Modifier = Modifier,
) {
    val color = FcTheme.colors.primary
    PawLoadingRow(modifier = modifier, color = color)
}

@Composable
private fun PawLoadingRow(
    modifier: Modifier = Modifier,
    color: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "paw_loading")

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, delayMillis = 0, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha1",
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, delayMillis = 167, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha2",
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, delayMillis = 333, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha3",
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PawPrintIcon(size = 14.dp, color = color.copy(alpha = alpha1))
        Spacer(modifier = Modifier.width(5.dp))
        PawPrintIcon(size = 12.dp, color = color.copy(alpha = alpha2))
        Spacer(modifier = Modifier.width(5.dp))
        PawPrintIcon(size = 10.dp, color = color.copy(alpha = alpha3))
    }
}

@Composable
private fun PawPrintIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        drawPawPrint(color = color)
    }
}

private fun DrawScope.drawPawPrint(color: Color) {
    val w = size.width
    val h = size.height

    // Main center pad
    drawCircle(
        color = color,
        radius = w * 0.28f,
        center = Offset(w * 0.5f, h * 0.68f),
    )
    // Toe pads
    val toeR = w * 0.16f
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.18f, h * 0.36f))
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.38f, h * 0.18f))
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.62f, h * 0.18f))
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.82f, h * 0.36f))
}

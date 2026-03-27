package com.kero.face.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class FaceChangerShapes(
    val xs: Shape = RoundedCornerShape(4.dp),
    val sm: Shape = RoundedCornerShape(8.dp),
    val md: Shape = RoundedCornerShape(12.dp),
    val lg: Shape = RoundedCornerShape(16.dp),
    val xl: Shape = RoundedCornerShape(24.dp),
    val pill: Shape = RoundedCornerShape(100.dp),
    val button: Shape = RoundedCornerShape(24.dp),
    val card: Shape = RoundedCornerShape(16.dp),
    val thumbnail: Shape = RoundedCornerShape(12.dp),
)

val LocalFaceChangerShapes = staticCompositionLocalOf { FaceChangerShapes() }

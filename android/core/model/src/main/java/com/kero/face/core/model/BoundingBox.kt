package com.kero.face.core.model

import android.graphics.RectF

data class BoundingBox(
    val rect: RectF,
    val label: String,
    val confidence: Float,
)

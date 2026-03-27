package com.kero.face.core.model

import android.graphics.PointF
import android.graphics.RectF

data class FaceLandmarks(
    val points: List<PointF>,
    val boundingBox: RectF,
)

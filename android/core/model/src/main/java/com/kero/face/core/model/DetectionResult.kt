package com.kero.face.core.model

import android.graphics.Bitmap

data class DetectionResult(
    val personFace: FaceLandmarks?,
    val dogBox: BoundingBox?,
    val frameBitmap: Bitmap?,
    val timestampMs: Long,
)

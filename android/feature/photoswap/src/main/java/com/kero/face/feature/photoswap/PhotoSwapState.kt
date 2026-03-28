package com.kero.face.feature.photoswap

import android.graphics.Bitmap
import com.kero.face.core.model.DetectionResult

data class PhotoSwapState(
    val bitmap: Bitmap? = null,
    val detectionResult: DetectionResult? = null,
    val isAnalyzing: Boolean = false,
    val error: String? = null,
    val debugInfo: String? = null,
)

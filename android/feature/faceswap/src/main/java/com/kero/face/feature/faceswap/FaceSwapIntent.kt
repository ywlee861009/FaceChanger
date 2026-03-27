package com.kero.face.feature.faceswap

import com.kero.face.core.model.DetectionResult

sealed interface FaceSwapIntent {
    data object StartCamera : FaceSwapIntent
    data object StopCamera : FaceSwapIntent
    data class OnFrameAnalyzed(val result: DetectionResult) : FaceSwapIntent
    data object DismissError : FaceSwapIntent
}

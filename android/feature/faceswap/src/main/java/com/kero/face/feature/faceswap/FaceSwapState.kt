package com.kero.face.feature.faceswap

import android.graphics.Bitmap
import com.kero.face.core.model.BoundingBox
import com.kero.face.core.model.FaceLandmarks

data class FaceSwapState(
    val cameraStatus: CameraStatus = CameraStatus.Initializing,
    val personFace: FaceLandmarks? = null,
    val dogBoundingBox: BoundingBox? = null,
    val guideMessage: GuideMessage = GuideMessage.WaitingForBoth,
    val isSwapEnabled: Boolean = false,
    val personCrop: Bitmap? = null,
    val dogCrop: Bitmap? = null,
    val frameWidth: Int = 0,
    val frameHeight: Int = 0,
    val error: UiError? = null,
)

enum class CameraStatus {
    Initializing,
    Ready,
    Error,
}

enum class GuideMessage {
    WaitingForBoth,
    NeedDog,
    NeedPerson,
    Ready,
}

data class UiError(val message: String)

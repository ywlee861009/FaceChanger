package com.kero.face.feature.faceswap

sealed interface FaceSwapEffect {
    data class ShowToast(val message: String) : FaceSwapEffect
}

package com.kero.face.feature.faceswap

sealed interface FaceSwapEffect {
    data class ShowToast(val message: String) : FaceSwapEffect
    data object NavigateToResult : FaceSwapEffect
    data object NavigateBack : FaceSwapEffect
}

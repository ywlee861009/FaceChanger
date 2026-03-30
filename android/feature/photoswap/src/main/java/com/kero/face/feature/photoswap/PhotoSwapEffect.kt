package com.kero.face.feature.photoswap

import android.graphics.Bitmap

sealed interface PhotoSwapEffect {
    data object NavigateBack : PhotoSwapEffect
    data class ShowError(val message: String) : PhotoSwapEffect
    data class NavigateToResult(val bitmap: Bitmap) : PhotoSwapEffect
}

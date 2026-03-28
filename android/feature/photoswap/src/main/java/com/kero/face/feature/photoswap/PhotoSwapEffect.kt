package com.kero.face.feature.photoswap

sealed interface PhotoSwapEffect {
    data object NavigateBack : PhotoSwapEffect
    data class ShowError(val message: String) : PhotoSwapEffect
}

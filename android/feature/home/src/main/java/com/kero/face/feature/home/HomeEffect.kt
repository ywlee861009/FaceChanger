package com.kero.face.feature.home

sealed interface HomeEffect {
    data object NavigateToLiveCamera : HomeEffect
    data object NavigateToPhotoSwap : HomeEffect
}

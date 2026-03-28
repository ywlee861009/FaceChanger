package com.kero.face.feature.home

sealed interface HomeIntent {
    data object ClickLiveCamera : HomeIntent
    data object ClickPhotoSwap : HomeIntent
}

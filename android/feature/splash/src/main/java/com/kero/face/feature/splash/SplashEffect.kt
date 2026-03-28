package com.kero.face.feature.splash

sealed interface SplashEffect {
    data object NavigateToHome : SplashEffect
}

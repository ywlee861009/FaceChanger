package com.kero.face.feature.splash

sealed interface SplashIntent {
    data object AnimationPhase1Done : SplashIntent
    data object AnimationPhase2Done : SplashIntent
    data object InitializationDone : SplashIntent
}

package com.kero.face.feature.splash

data class SplashState(
    val phase: SplashPhase = SplashPhase.FACE_SWAP,
    val isInitialized: Boolean = false,
)

enum class SplashPhase {
    FACE_SWAP,  // Phase 1: 얼굴 교환 애니메이션 (0ms ~ 1000ms)
    TITLE_IN,   // Phase 2: 타이틀 등장 + 얼굴 페이드아웃 (1000ms ~ 1500ms)
    DONE,       // Phase 3: 완료 → 홈 화면으로 전환
}

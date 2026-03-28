package com.kero.face.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect: Flow<SplashEffect> = _effect.receiveAsFlow()

    private val phase1Done = MutableStateFlow(false)
    private val initDone = MutableStateFlow(false)

    init {
        startInitialization()
        observeReadyToAdvance()
    }

    fun dispatch(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.AnimationPhase1Done -> phase1Done.value = true
            is SplashIntent.InitializationDone -> initDone.value = true
            is SplashIntent.AnimationPhase2Done -> {
                viewModelScope.launch {
                    _state.update { it.copy(phase = SplashPhase.DONE) }
                    _effect.send(SplashEffect.NavigateToHome)
                }
            }
        }
    }

    private fun observeReadyToAdvance() {
        combine(phase1Done, initDone) { p1, init -> p1 && init }
            .filter { it }
            .onEach {
                _state.update { it.copy(phase = SplashPhase.TITLE_IN, isInitialized = true) }
            }
            .launchIn(viewModelScope)
    }

    private fun startInitialization() {
        viewModelScope.launch {
            // 앱 초기화 작업 (최소 1500ms 또는 실제 작업 완료 중 늦은 시점)
            delay(1500L)
            dispatch(SplashIntent.InitializationDone)
        }
    }
}

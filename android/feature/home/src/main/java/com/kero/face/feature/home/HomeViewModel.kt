package com.kero.face.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ClickLiveCamera -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToLiveCamera)
            }
            HomeIntent.ClickPhotoSwap -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToPhotoSwap)
            }
        }
    }
}

package com.kero.face.feature.photoswap

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoSwapViewModel : ViewModel() {

    private val _state = MutableStateFlow(PhotoSwapState())
    val state: StateFlow<PhotoSwapState> = _state.asStateFlow()

    private val _effect = Channel<PhotoSwapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(intent: PhotoSwapIntent) {
        when (intent) {
            is PhotoSwapIntent.DogPhotoSelected -> {
                _state.update { it.copy(dogPhotoUri = intent.uri, error = null) }
            }
            is PhotoSwapIntent.PersonPhotoSelected -> {
                _state.update { it.copy(personPhotoUri = intent.uri, error = null) }
            }
            PhotoSwapIntent.StartSwap -> startSwap()
            PhotoSwapIntent.NavigateBack -> viewModelScope.launch {
                _effect.send(PhotoSwapEffect.NavigateBack)
            }
        }
    }

    private fun startSwap() {
        val state = _state.value
        if (!state.isSwapEnabled) return

        // TODO: 실제 얼굴 교환 처리 구현
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            _effect.send(PhotoSwapEffect.NavigateToResult)
        }
    }
}

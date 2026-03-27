package com.kero.face.feature.faceswap

import androidx.lifecycle.ViewModel
import com.kero.face.feature.faceswap.internal.FaceSwapReducer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class FaceSwapViewModel : ViewModel() {

    private val _state = MutableStateFlow(FaceSwapState())
    val state: StateFlow<FaceSwapState> = _state.asStateFlow()

    private val _effect = Channel<FaceSwapEffect>(Channel.BUFFERED)
    val effect: Flow<FaceSwapEffect> = _effect.receiveAsFlow()

    fun dispatch(intent: FaceSwapIntent) {
        when (intent) {
            is FaceSwapIntent.StartCamera -> {
                _state.update { it.copy(cameraStatus = CameraStatus.Ready) }
            }
            is FaceSwapIntent.StopCamera -> {
                _state.update { it.copy(cameraStatus = CameraStatus.Initializing) }
            }
            is FaceSwapIntent.OnFrameAnalyzed -> {
                _state.update { current ->
                    FaceSwapReducer.reduceDetectionResult(current, intent.result)
                }
            }
            is FaceSwapIntent.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }
}

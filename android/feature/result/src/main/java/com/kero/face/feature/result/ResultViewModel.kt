package com.kero.face.feature.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _effect = Channel<ResultEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(intent: ResultIntent) {
        when (intent) {
            ResultIntent.Save -> saveResult()
            ResultIntent.Share -> viewModelScope.launch {
                _effect.send(ResultEffect.ShareResult)
            }
            ResultIntent.Retry -> viewModelScope.launch {
                _effect.send(ResultEffect.NavigateBack)
            }
        }
    }

    private fun saveResult() {
        // TODO: 갤러리 저장 구현
        viewModelScope.launch {
            _effect.send(ResultEffect.ShowMessage("저장되었습니다"))
        }
    }
}

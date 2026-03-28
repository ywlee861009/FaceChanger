package com.kero.face.feature.photoswap

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kero.face.core.ml.DetectionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoSwapViewModel(application: Application) : AndroidViewModel(application) {

    private val detectionEngine = DetectionEngine.create(application)

    private val _state = MutableStateFlow(PhotoSwapState())
    val state: StateFlow<PhotoSwapState> = _state.asStateFlow()

    private val _effect = Channel<PhotoSwapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(intent: PhotoSwapIntent) {
        when (intent) {
            is PhotoSwapIntent.PhotoSelected -> analyzePhoto(intent.uri)
            PhotoSwapIntent.PickerCancelled -> {
                // 사진이 아직 없을 때 취소하면 뒤로가기, 이미 있으면 현재 사진 유지
                if (_state.value.bitmap == null) {
                    viewModelScope.launch { _effect.send(PhotoSwapEffect.NavigateBack) }
                }
            }
            PhotoSwapIntent.NavigateBack -> viewModelScope.launch {
                _effect.send(PhotoSwapEffect.NavigateBack)
            }
        }
    }

    private fun analyzePhoto(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isAnalyzing = true, error = null) }
            try {
                val bitmap = loadBitmap(uri) ?: error("이미지를 불러올 수 없습니다")
                val result = detectionEngine.detectBitmap(bitmap)
                _state.update {
                    it.copy(bitmap = bitmap, detectionResult = result, isAnalyzing = false)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false, error = e.message) }
                _effect.send(PhotoSwapEffect.ShowError(e.message ?: "오류가 발생했습니다"))
            }
        }
    }

    private fun loadBitmap(uri: Uri): Bitmap? = try {
        getApplication<Application>().contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    } catch (e: Exception) {
        null
    }

    override fun onCleared() {
        super.onCleared()
        detectionEngine.close()
    }
}

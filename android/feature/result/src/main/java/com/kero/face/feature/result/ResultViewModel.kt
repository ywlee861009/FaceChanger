package com.kero.face.feature.result

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _effect = Channel<ResultEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun dispatch(intent: ResultIntent) {
        when (intent) {
            is ResultIntent.Initialize -> _state.update { it.copy(bitmap = intent.bitmap) }
            ResultIntent.Save -> saveResult()
            ResultIntent.Share -> shareResult()
            ResultIntent.Retry -> viewModelScope.launch {
                _effect.send(ResultEffect.NavigateBack)
            }
        }
    }

    private fun saveResult() {
        val bitmap = _state.value.bitmap ?: return
        if (_state.value.isSaved) {
            viewModelScope.launch { _effect.send(ResultEffect.ShowMessage("이미 저장되었습니다")) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = saveBitmapToGallery(bitmap)
                _state.update { it.copy(isSaving = false, isSaved = true, savedUri = uri) }
                _effect.send(ResultEffect.ShowMessage("갤러리에 저장되었습니다"))
            } catch (e: Exception) {
                android.util.Log.e("ResultViewModel", "갤러리 저장 실패", e)
                _state.update { it.copy(isSaving = false, error = e.message) }
                _effect.send(ResultEffect.ShowMessage("저장 실패: ${e.message}"))
            }
        }
    }

    private fun shareResult() {
        val savedUri = _state.value.savedUri
        if (savedUri != null) {
            viewModelScope.launch { _effect.send(ResultEffect.ShareResult(savedUri)) }
            return
        }
        // 아직 저장 안 됐으면 저장 후 공유
        val bitmap = _state.value.bitmap ?: run {
            viewModelScope.launch { _effect.send(ResultEffect.ShareResult(null)) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = saveBitmapToGallery(bitmap)
                _state.update { it.copy(isSaving = false, isSaved = true, savedUri = uri) }
                _effect.send(ResultEffect.ShareResult(uri))
            } catch (e: Exception) {
                android.util.Log.e("ResultViewModel", "공유용 저장 실패", e)
                _state.update { it.copy(isSaving = false) }
                _effect.send(ResultEffect.ShareResult(null))
            }
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): Uri {
        val app = getApplication<Application>()
        val fileName = "FaceChanger_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FaceChanger")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = app.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        ) ?: error("MediaStore insert 실패")

        app.contentResolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        } ?: error("OutputStream 열기 실패")

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        app.contentResolver.update(uri, contentValues, null, null)
        return uri
    }
}

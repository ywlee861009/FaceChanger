package com.kero.face.feature.photoswap

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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
            _state.update { it.copy(isAnalyzing = true, error = null, debugInfo = null) }
            try {
                val (bitmap, loadLog) = loadBitmap(uri) ?: error("이미지를 불러올 수 없습니다")
                val bitmapInfo = "비트맵: ${bitmap.width}x${bitmap.height}, 포맷: ${bitmap.config}\n$loadLog"
                android.util.Log.d("PhotoSwap", "analyzePhoto: $bitmapInfo")
                val result = detectionEngine.detectBitmap(bitmap)
                val faceInfo = result.personFace?.let { f ->
                    "감지됨 box=(${f.boundingBox.left.fmt()},${f.boundingBox.top.fmt()},${f.boundingBox.right.fmt()},${f.boundingBox.bottom.fmt()})"
                } ?: "미감지"
                val dogInfo = result.dogBox?.let { d ->
                    "감지됨 conf=${d.confidence.fmt()} label=${d.label}"
                } ?: "미감지"
                val debugInfo = buildString {
                    appendLine("[디버그 정보]")
                    appendLine(bitmapInfo)
                    appendLine("사람 얼굴: $faceInfo")
                    appendLine("강아지: $dogInfo")
                }
                android.util.Log.d("PhotoSwap", debugInfo)
                _state.update {
                    it.copy(bitmap = bitmap, detectionResult = result, isAnalyzing = false, debugInfo = debugInfo)
                }
            } catch (e: Exception) {
                val errMsg = "${e::class.simpleName}: ${e.message}"
                val stackTrace = e.stackTrace.take(5).joinToString("\n") { "  at ${it.className}.${it.methodName}:${it.lineNumber}" }
                val debugInfo = "[오류 발생]\n$errMsg\n$stackTrace"
                android.util.Log.e("PhotoSwap", "analyzePhoto 오류", e)
                _state.update { it.copy(isAnalyzing = false, error = errMsg, debugInfo = debugInfo) }
                _effect.send(PhotoSwapEffect.ShowError(errMsg))
            }
        }
    }

    private fun Float.fmt() = "%.3f".format(this)

    /** 비트맵 로드 + EXIF 회전 + 리사이즈. Pair<Bitmap, 로그> 반환 */
    private fun loadBitmap(uri: Uri): Pair<Bitmap, String>? {
        val app = getApplication<Application>()
        return try {
            // 1) 원본 디코드
            val raw = app.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                ?: return null
            val logs = mutableListOf("원본: ${raw.width}x${raw.height}")

            // 2) EXIF 회전 읽기 (API 24+ 프레임워크 ExifInterface 사용)
            val rotation = try {
                app.contentResolver.openInputStream(uri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                        ExifInterface.ORIENTATION_ROTATE_90  -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                } ?: 0f
            } catch (e: Exception) {
                logs.add("EXIF 읽기 실패: ${e.message}")
                0f
            }
            logs.add("EXIF 회전: ${rotation.toInt()}°")

            // 3) 회전 적용
            val rotated = if (rotation != 0f) {
                val m = Matrix().apply { postRotate(rotation) }
                Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, m, true)
                    .also { if (it !== raw) raw.recycle() }
            } else raw

            // 4) 너무 크면 리사이즈 (MediaPipe 안정성 + 메모리)
            val maxDim = 1280
            val resized = if (maxOf(rotated.width, rotated.height) > maxDim) {
                val scale = maxDim.toFloat() / maxOf(rotated.width, rotated.height)
                val w = (rotated.width * scale).toInt()
                val h = (rotated.height * scale).toInt()
                logs.add("리사이즈: ${rotated.width}x${rotated.height} → ${w}x${h}")
                Bitmap.createScaledBitmap(rotated, w, h, true)
                    .also { if (it !== rotated) rotated.recycle() }
            } else rotated

            android.util.Log.d("PhotoSwap", "loadBitmap: ${logs.joinToString(", ")}")
            Pair(resized, logs.joinToString("\n"))
        } catch (e: Exception) {
            android.util.Log.e("PhotoSwap", "loadBitmap 오류", e)
            null
        }
    }

    override fun onCleared() {
        super.onCleared()
        detectionEngine.close()
    }
}

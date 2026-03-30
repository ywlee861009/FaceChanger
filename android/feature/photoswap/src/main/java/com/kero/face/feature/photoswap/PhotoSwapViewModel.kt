package com.kero.face.feature.photoswap

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kero.face.core.ml.DetectionEngine
import com.kero.face.core.model.BoundingBox
import com.kero.face.core.model.FaceLandmarks
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
            PhotoSwapIntent.PerformSwap -> performSwap()
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

    private fun performSwap() {
        val state = _state.value
        val bitmap = state.bitmap ?: return
        val humanFace = state.detectionResult?.personFace ?: return
        val dogBox = state.detectionResult?.dogBox ?: return

        viewModelScope.launch(Dispatchers.Default) {
            _state.update { it.copy(isSwapping = true) }
            try {
                val swapped = applyFaceSwap(bitmap, humanFace, dogBox)
                _effect.send(PhotoSwapEffect.NavigateToResult(swapped))
            } catch (e: Exception) {
                android.util.Log.e("PhotoSwap", "얼굴 교환 실패", e)
                _state.update { it.copy(isSwapping = false, error = "교환 실패: ${e.message}") }
            }
        }
    }

    /**
     * 강아지 얼굴 영역을 잘라내어 사람 얼굴 영역에 타원형으로 붙여넣은 비트맵 반환.
     * - humanFace.boundingBox: 정규화 좌표 [0,1]
     * - dogBox.rect: 픽셀 좌표
     */
    private fun applyFaceSwap(
        bitmap: Bitmap,
        humanFace: FaceLandmarks,
        dogBox: BoundingBox,
    ): Bitmap {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()

        // 사람 얼굴 영역 (정규화 → 픽셀, 여백 10% 추가)
        val hBox = humanFace.boundingBox
        val marginX = hBox.width() * 0.1f
        val marginY = hBox.height() * 0.1f
        val hLeft = ((hBox.left - marginX) * w).toInt().coerceIn(0, bitmap.width)
        val hTop = ((hBox.top - marginY) * h).toInt().coerceIn(0, bitmap.height)
        val hRight = ((hBox.right + marginX) * w).toInt().coerceIn(0, bitmap.width)
        val hBottom = ((hBox.bottom + marginY) * h).toInt().coerceIn(0, bitmap.height)
        val hWidth = (hRight - hLeft).coerceAtLeast(1)
        val hHeight = (hBottom - hTop).coerceAtLeast(1)

        // 강아지 얼굴 영역 (픽셀 좌표)
        val dBox = dogBox.rect
        val dLeft = dBox.left.toInt().coerceIn(0, bitmap.width)
        val dTop = dBox.top.toInt().coerceIn(0, bitmap.height)
        val dRight = dBox.right.toInt().coerceIn(0, bitmap.width)
        val dBottom = dBox.bottom.toInt().coerceIn(0, bitmap.height)
        val dWidth = (dRight - dLeft).coerceAtLeast(1)
        val dHeight = (dBottom - dTop).coerceAtLeast(1)

        // 강아지 얼굴 크롭 → 사람 얼굴 크기에 맞게 스케일
        val dogCrop = Bitmap.createBitmap(bitmap, dLeft, dTop, dWidth, dHeight)
        val scaledDog = Bitmap.createScaledBitmap(dogCrop, hWidth, hHeight, true)
        dogCrop.recycle()

        // 원본 복사 후 타원 클립으로 강아지 얼굴을 자연스럽게 붙여넣기
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val clipPath = Path().apply {
            addOval(
                RectF(hLeft.toFloat(), hTop.toFloat(), hRight.toFloat(), hBottom.toFloat()),
                Path.Direction.CW,
            )
        }
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawBitmap(scaledDog, hLeft.toFloat(), hTop.toFloat(), null)
        canvas.restore()
        scaledDog.recycle()

        return result
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

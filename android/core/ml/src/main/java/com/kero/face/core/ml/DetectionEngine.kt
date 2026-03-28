package com.kero.face.core.ml

import android.content.Context
import android.graphics.Bitmap
import com.kero.face.core.model.DetectionResult
import com.kero.face.core.ml.internal.DetectionEngineImpl

interface DetectionEngine {

    fun initialize()

    fun processFrame(
        frameBuffer: java.nio.ByteBuffer,
        width: Int,
        height: Int,
        timestampMs: Long,
        rotationDegrees: Int = 0,
        onResult: (DetectionResult) -> Unit,
    )

    /** 정지 이미지에서 얼굴과 강아지를 동기적으로 감지합니다. */
    fun detectBitmap(bitmap: Bitmap): DetectionResult

    fun close()

    companion object {
        fun create(context: Context): DetectionEngine = DetectionEngineImpl(context)
    }
}

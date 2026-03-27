package com.kero.face.core.ml

import android.content.Context
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

    fun close()

    companion object {
        fun create(context: Context): DetectionEngine = DetectionEngineImpl(context)
    }
}

package com.kero.face.core.ml.internal

import android.content.Context
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.kero.face.core.ml.DetectionEngine
import com.kero.face.core.model.DetectionResult
import android.graphics.Bitmap
import java.nio.ByteBuffer

internal class DetectionEngineImpl(private val context: Context) : DetectionEngine {

    private val faceLandmarkerProcessor = FaceLandmarkerProcessor(context)
    private val dogDetectorProcessor = DogDetectorProcessor(context)

    override fun initialize() {
        faceLandmarkerProcessor.initialize()
        dogDetectorProcessor.initialize()
    }

    override fun processFrame(
        frameBuffer: ByteBuffer,
        width: Int,
        height: Int,
        timestampMs: Long,
        onResult: (DetectionResult) -> Unit,
    ) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        frameBuffer.rewind()
        bitmap.copyPixelsFromBuffer(frameBuffer)

        val mpImage = BitmapImageBuilder(bitmap).build()

        faceLandmarkerProcessor.processFrame(mpImage, timestampMs)
        dogDetectorProcessor.processFrame(mpImage, timestampMs)

        val result = DetectionResult(
            personFace = faceLandmarkerProcessor.getLatestResult(),
            dogBox = dogDetectorProcessor.getLatestResult(),
            frameBitmap = bitmap,
            timestampMs = timestampMs,
        )
        onResult(result)
    }

    override fun close() {
        faceLandmarkerProcessor.close()
        dogDetectorProcessor.close()
    }
}

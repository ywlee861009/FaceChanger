package com.kero.face.core.ml.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.kero.face.core.ml.DetectionEngine
import com.kero.face.core.model.DetectionResult
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
        rotationDegrees: Int,
        onResult: (DetectionResult) -> Unit,
    ) {
        val rawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        frameBuffer.rewind()
        rawBitmap.copyPixelsFromBuffer(frameBuffer)

        val bitmap = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(rawBitmap, 0, 0, width, height, matrix, true).also {
                if (it !== rawBitmap) rawBitmap.recycle()
            }
        } else {
            rawBitmap
        }

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

package com.kero.face.core.ml.internal

import android.content.Context
import android.graphics.RectF
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.kero.face.core.model.BoundingBox

internal class DogDetectorProcessor(private val context: Context) {

    private var objectDetector: ObjectDetector? = null
    @Volatile private var latestResult: BoundingBox? = null

    // EMA 평활화: bbox 흔들림 감소
    private var smoothedRect: RectF? = null
    private val smoothingAlpha = 0.4f  // 낮을수록 부드럽지만 반응이 느림

    fun initialize() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("efficientdet_lite2.tflite")
            .build()

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)
            .setMaxResults(5)
            .setScoreThreshold(0.4f)
            .setResultListener { result, _ -> handleResult(result) }
            .setErrorListener { error -> error.printStackTrace() }
            .build()

        objectDetector = ObjectDetector.createFromOptions(context, options)
    }

    fun processFrame(mpImage: MPImage, timestampMs: Long) {
        objectDetector?.detectAsync(mpImage, timestampMs)
    }

    fun getLatestResult(): BoundingBox? = latestResult

    fun close() {
        objectDetector?.close()
        objectDetector = null
        smoothedRect = null
    }

    private fun handleResult(result: ObjectDetectorResult) {
        val dogDetection = result.detections().firstOrNull { detection ->
            detection.categories().any { category ->
                category.categoryName().equals("dog", ignoreCase = true)
            }
        }

        if (dogDetection == null) {
            latestResult = null
            smoothedRect = null
            return
        }

        val bbox = dogDetection.boundingBox()
        val dogCategory = dogDetection.categories().first { it.categoryName().equals("dog", ignoreCase = true) }

        val rawRect = RectF(bbox.left, bbox.top, bbox.right, bbox.bottom)
        val smoothed = smoothRect(rawRect)

        latestResult = BoundingBox(
            rect = smoothed,
            label = "dog",
            confidence = dogCategory.score(),
        )
    }

    private fun smoothRect(newRect: RectF): RectF {
        val prev = smoothedRect
        return if (prev == null) {
            RectF(newRect).also { smoothedRect = it }
        } else {
            RectF(
                prev.left + smoothingAlpha * (newRect.left - prev.left),
                prev.top + smoothingAlpha * (newRect.top - prev.top),
                prev.right + smoothingAlpha * (newRect.right - prev.right),
                prev.bottom + smoothingAlpha * (newRect.bottom - prev.bottom),
            ).also { smoothedRect = it }
        }
    }
}

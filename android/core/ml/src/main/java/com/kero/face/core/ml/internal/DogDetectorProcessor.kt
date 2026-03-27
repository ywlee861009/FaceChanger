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
    private var latestResult: BoundingBox? = null

    fun initialize() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("efficientdet_lite0.tflite")
            .build()

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)
            .setMaxResults(5)
            .setScoreThreshold(0.3f)
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
    }

    private fun handleResult(result: ObjectDetectorResult) {
        val dogDetection = result.detections().firstOrNull { detection ->
            detection.categories().any { category ->
                category.categoryName().equals("dog", ignoreCase = true)
            }
        }

        if (dogDetection == null) {
            latestResult = null
            return
        }

        val bbox = dogDetection.boundingBox()
        val dogCategory = dogDetection.categories().first { it.categoryName().equals("dog", ignoreCase = true) }

        latestResult = BoundingBox(
            rect = RectF(
                bbox.left.toFloat() / 1f,
                bbox.top.toFloat() / 1f,
                bbox.right.toFloat() / 1f,
                bbox.bottom.toFloat() / 1f,
            ),
            label = "dog",
            confidence = dogCategory.score(),
        )
    }
}

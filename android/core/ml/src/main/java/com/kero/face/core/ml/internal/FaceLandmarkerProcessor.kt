package com.kero.face.core.ml.internal

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.kero.face.core.model.FaceLandmarks
import java.nio.ByteBuffer

internal class FaceLandmarkerProcessor(private val context: Context) {

    private var faceLandmarker: FaceLandmarker? = null
    @Volatile private var latestResult: FaceLandmarks? = null

    fun initialize() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)
            .setNumFaces(1)
            .setResultListener { result, _ -> handleResult(result) }
            .setErrorListener { error -> error.printStackTrace() }
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(context, options)
    }

    fun processFrame(mpImage: MPImage, timestampMs: Long) {
        faceLandmarker?.detectAsync(mpImage, timestampMs)
    }

    fun getLatestResult(): FaceLandmarks? = latestResult

    fun close() {
        faceLandmarker?.close()
        faceLandmarker = null
    }

    private fun handleResult(result: FaceLandmarkerResult) {
        if (result.faceLandmarks().isEmpty()) {
            latestResult = null
            return
        }

        val landmarks = result.faceLandmarks()[0]
        val points = landmarks.map { landmark ->
            PointF(landmark.x(), landmark.y())
        }

        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        for (point in points) {
            if (point.x < minX) minX = point.x
            if (point.y < minY) minY = point.y
            if (point.x > maxX) maxX = point.x
            if (point.y > maxY) maxY = point.y
        }

        latestResult = FaceLandmarks(
            points = points,
            boundingBox = RectF(minX, minY, maxX, maxY),
        )
    }
}

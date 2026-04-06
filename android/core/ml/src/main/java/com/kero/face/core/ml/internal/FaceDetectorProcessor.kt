package com.kero.face.core.ml.internal

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.kero.face.core.model.FaceLandmarks

internal class FaceDetectorProcessor(private val context: Context) {

    private var faceDetector: FaceDetector? = null
    @Volatile private var latestResult: FaceLandmarks? = null

    fun initialize() {
        val options = FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(
                BaseOptions.builder().setModelAssetPath("blaze_face_short_range.tflite").build()
            )
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setMinDetectionConfidence(0.3f)
            .setResultListener { result, image -> handleResult(result, image) }
            .setErrorListener { error -> error.printStackTrace() }
            .build()
        faceDetector = FaceDetector.createFromOptions(context, options)
    }

    fun processFrame(mpImage: MPImage, timestampMs: Long) {
        faceDetector?.detectAsync(mpImage, timestampMs)
    }

    fun getLatestResult(): FaceLandmarks? = latestResult

    fun close() {
        faceDetector?.close()
        faceDetector = null
    }

    private fun handleResult(result: FaceDetectorResult, image: MPImage) {
        if (result.detections().isEmpty()) {
            latestResult = null
            return
        }
        val detection = result.detections()[0]
        val bbox = detection.boundingBox()
        val w = image.width.toFloat()
        val h = image.height.toFloat()
        val normalizedBox = RectF(
            bbox.left / w, bbox.top / h,
            bbox.right / w, bbox.bottom / h,
        )
        val keypoints = detection.keypoints()
            .map { list -> list.map { PointF(it.x(), it.y()) } }
            .orElse(emptyList())
        latestResult = FaceLandmarks(points = keypoints, boundingBox = normalizedBox)
    }
}

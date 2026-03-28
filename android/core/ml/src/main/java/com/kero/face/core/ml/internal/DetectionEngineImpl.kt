package com.kero.face.core.ml.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.kero.face.core.ml.DetectionEngine
import com.kero.face.core.model.BoundingBox
import com.kero.face.core.model.DetectionResult
import com.kero.face.core.model.FaceLandmarks
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

    override fun detectBitmap(bitmap: Bitmap): DetectionResult {
        val mpImage = BitmapImageBuilder(bitmap).build()
        val faceResult = detectFaceInImage(mpImage)
        val dogResult = detectDogInImage(mpImage)
        return DetectionResult(
            personFace = faceResult,
            dogBox = dogResult,
            frameBitmap = bitmap,
            timestampMs = System.currentTimeMillis(),
        )
    }

    private fun detectFaceInImage(mpImage: MPImage): FaceLandmarks? {
        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(
                BaseOptions.builder().setModelAssetPath("face_landmarker.task").build()
            )
            .setRunningMode(RunningMode.IMAGE)
            .setNumFaces(1)
            .build()
        val detector = FaceLandmarker.createFromOptions(context, options)
        return try {
            val result = detector.detect(mpImage)
            if (result.faceLandmarks().isEmpty()) return null
            val landmarks = result.faceLandmarks()[0]
            val points = landmarks.map { PointF(it.x(), it.y()) }
            var minX = Float.MAX_VALUE; var minY = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
            for (p in points) {
                if (p.x < minX) minX = p.x
                if (p.y < minY) minY = p.y
                if (p.x > maxX) maxX = p.x
                if (p.y > maxY) maxY = p.y
            }
            FaceLandmarks(points = points, boundingBox = RectF(minX, minY, maxX, maxY))
        } finally {
            detector.close()
        }
    }

    private fun detectDogInImage(mpImage: MPImage): BoundingBox? {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(
                BaseOptions.builder().setModelAssetPath("efficientdet_lite0.tflite").build()
            )
            .setRunningMode(RunningMode.IMAGE)
            .setMaxResults(5)
            .setScoreThreshold(0.3f)
            .build()
        val detector = ObjectDetector.createFromOptions(context, options)
        return try {
            val result = detector.detect(mpImage)
            val dogDetection = result.detections().firstOrNull { detection ->
                detection.categories().any { it.categoryName().equals("dog", ignoreCase = true) }
            } ?: return null
            val bbox = dogDetection.boundingBox()
            val dogCategory = dogDetection.categories()
                .first { it.categoryName().equals("dog", ignoreCase = true) }
            // TODO: dog face 전용 모델로 교체 시 estimateDogFaceRect() 제거하고
            //       모델 결과를 직접 사용하면 됨. 호출부(DetectionResult.dogBox) 변경 없음.
            val faceRect = estimateDogFaceRect(RectF(bbox.left, bbox.top, bbox.right, bbox.bottom))
            BoundingBox(
                rect = faceRect,
                label = "dog_face",
                confidence = dogCategory.score(),
            )
        } finally {
            detector.close()
        }
    }

    /**
     * 강아지 전신 bbox에서 얼굴 영역을 추정합니다.
     * 강아지 얼굴 전용 모델로 교체할 때 이 함수만 제거하면 됩니다.
     *
     * 추정 근거: 강아지 얼굴은 전신 bbox의 상단 35%, 가로 중앙 60% 영역에 위치.
     */
    private fun estimateDogFaceRect(bodyRect: RectF): RectF {
        val cx = bodyRect.centerX()
        val faceW = bodyRect.width() * 0.6f
        val faceH = bodyRect.height() * 0.35f
        return RectF(
            cx - faceW / 2f,
            bodyRect.top,
            cx + faceW / 2f,
            bodyRect.top + faceH,
        )
    }

    override fun close() {
        faceLandmarkerProcessor.close()
        dogDetectorProcessor.close()
    }
}

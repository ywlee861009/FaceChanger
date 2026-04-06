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
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.kero.face.core.ml.DetectionEngine
import com.kero.face.core.model.BoundingBox
import com.kero.face.core.model.DetectionResult
import com.kero.face.core.model.FaceLandmarks
import java.nio.ByteBuffer

internal class DetectionEngineImpl(private val context: Context) : DetectionEngine {

    private val faceDetectorProcessor = FaceDetectorProcessor(context)
    private val dogDetectorProcessor = DogDetectorProcessor(context)

    override fun initialize() {
        faceDetectorProcessor.initialize()
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

        faceDetectorProcessor.processFrame(mpImage, timestampMs)
        dogDetectorProcessor.processFrame(mpImage, timestampMs)

        val result = DetectionResult(
            personFace = faceDetectorProcessor.getLatestResult(),
            dogBox = dogDetectorProcessor.getLatestResult(),
            frameBitmap = bitmap,
            timestampMs = timestampMs,
        )
        onResult(result)
    }

    override fun detectBitmap(bitmap: Bitmap): DetectionResult {
        val argbBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            android.util.Log.w("DetectionEngine", "비트맵 포맷 변환: ${bitmap.config} → ARGB_8888")
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }
        val faceMpImage = BitmapImageBuilder(argbBitmap).build()
        val dogMpImage = BitmapImageBuilder(argbBitmap).build()
        val faceResult = detectFaceInImage(faceMpImage)
        val dogResult = detectDogInImage(dogMpImage)
        android.util.Log.d("DetectionEngine", "detectBitmap 결과: face=$faceResult, dog=$dogResult")
        return DetectionResult(
            personFace = faceResult,
            dogBox = dogResult,
            frameBitmap = bitmap,
            timestampMs = System.currentTimeMillis(),
        )
    }

    private fun detectFaceInImage(mpImage: MPImage): FaceLandmarks? {
        val options = FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(
                BaseOptions.builder().setModelAssetPath("blaze_face_short_range.tflite").build()
            )
            .setRunningMode(RunningMode.IMAGE)
            .setMinDetectionConfidence(0.3f)
            .build()
        val detector = FaceDetector.createFromOptions(context, options)
        return try {
            val result = detector.detect(mpImage)
            android.util.Log.d("DetectionEngine", "FaceDetector 결과: ${result.detections().size}개 얼굴")
            if (result.detections().isEmpty()) return null
            val detection = result.detections()[0]
            val bbox = detection.boundingBox()
            val w = mpImage.width.toFloat()
            val h = mpImage.height.toFloat()
            val normalizedBox = RectF(
                bbox.left / w, bbox.top / h,
                bbox.right / w, bbox.bottom / h,
            )
            val keypoints = detection.keypoints()
                .map { list -> list.map { PointF(it.x(), it.y()) } }
                .orElse(emptyList())
            FaceLandmarks(points = keypoints, boundingBox = normalizedBox)
        } catch (e: Exception) {
            android.util.Log.e("DetectionEngine", "FaceDetector 오류", e)
            throw e
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
            .setMaxResults(10)
            .setScoreThreshold(0.1f)
            .build()
        val detector = ObjectDetector.createFromOptions(context, options)
        return try {
            val result = detector.detect(mpImage)
            android.util.Log.d("DetectionEngine", "ObjectDetector 결과: ${result.detections().size}개 감지")
            result.detections().forEachIndexed { i, det ->
                android.util.Log.d("DetectionEngine", "  [$i] categories=${det.categories().map { "${it.categoryName()}(${it.score()})" }}")
            }
            val dogDetection = result.detections().firstOrNull { detection ->
                detection.categories().any { it.categoryName().equals("dog", ignoreCase = true) }
            } ?: return null
            val bbox = dogDetection.boundingBox()
            val dogCategory = dogDetection.categories()
                .first { it.categoryName().equals("dog", ignoreCase = true) }
            val faceRect = estimateDogFaceRect(RectF(bbox.left, bbox.top, bbox.right, bbox.bottom))
            BoundingBox(
                rect = faceRect,
                label = "dog_face",
                confidence = dogCategory.score(),
            )
        } catch (e: Exception) {
            android.util.Log.e("DetectionEngine", "ObjectDetector 오류", e)
            throw e
        } finally {
            detector.close()
        }
    }

    /**
     * 강아지 전신 bbox에서 얼굴 영역을 추정합니다.
     *
     * 자세 추정:
     * - aspectRatio > 1.3 (가로 긴 bbox): 누운 자세 → bbox 상단 50%, 가로 55%
     * - aspectRatio 0.8~1.3 (정사각형): 카메라 정면 클로즈업 → 상단 70%, 가로 75%
     * - aspectRatio < 0.8 (세로 긴 bbox): 서있음/앉음 → 상단 28%, 가로 60%
     */
    private fun estimateDogFaceRect(bodyRect: RectF): RectF {
        val cx = bodyRect.centerX()
        val bboxW = bodyRect.width()
        val bboxH = bodyRect.height()
        val aspectRatio = bboxW / bboxH.coerceAtLeast(1f)

        val (faceW, faceH) = when {
            aspectRatio > 1.3f -> Pair(bboxW * 0.55f, bboxH * 0.50f)
            aspectRatio > 0.8f -> Pair(bboxW * 0.75f, bboxH * 0.70f)
            else -> Pair(bboxW * 0.60f, bboxH * 0.28f)
        }

        return RectF(
            cx - faceW / 2f,
            bodyRect.top,
            cx + faceW / 2f,
            bodyRect.top + faceH,
        )
    }

    override fun close() {
        faceDetectorProcessor.close()
        dogDetectorProcessor.close()
    }
}

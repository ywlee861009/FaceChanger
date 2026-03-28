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
        // ARGB_8888이 아닌 경우 변환 (MediaPipe 요구사항)
        val argbBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            android.util.Log.w("DetectionEngine", "비트맵 포맷 변환: ${bitmap.config} → ARGB_8888")
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }
        // face/dog 에 각각 별도 MPImage 생성 (동일 인스턴스 재사용 시 두 번째 호출 실패 가능)
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
            android.util.Log.d("DetectionEngine", "FaceLandmarker 결과: ${result.faceLandmarks().size}개 얼굴")
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
        } catch (e: Exception) {
            android.util.Log.e("DetectionEngine", "FaceLandmarker 오류", e)
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
            // TODO: dog face 전용 모델로 교체 시 estimateDogFaceRect() 제거하고
            //       모델 결과를 직접 사용하면 됨. 호출부(DetectionResult.dogBox) 변경 없음.
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
     * 강아지 얼굴 전용 모델로 교체할 때 이 함수만 제거하면 됩니다.
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
            aspectRatio > 1.3f -> {
                // 누운 자세: 가로가 훨씬 길면 bbox 위쪽 절반이 머리 영역
                Pair(bboxW * 0.55f, bboxH * 0.50f)
            }
            aspectRatio > 0.8f -> {
                // 정사각형에 가까운 경우: 클로즈업이거나 앉은 정면 → bbox 대부분이 머리
                Pair(bboxW * 0.75f, bboxH * 0.70f)
            }
            else -> {
                // 세로 긴 전신 사진: 서있거나 앉은 측면 → 상단 28%
                Pair(bboxW * 0.60f, bboxH * 0.28f)
            }
        }

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

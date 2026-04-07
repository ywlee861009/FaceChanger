package com.kero.face.feature.faceswap.internal

import android.graphics.Bitmap
import android.graphics.RectF
import com.kero.face.core.model.DetectionResult
import com.kero.face.feature.faceswap.FaceSwapState
import com.kero.face.feature.faceswap.GuideMessage
import kotlin.math.max
import kotlin.math.min

internal object FaceSwapReducer {

    fun reduceDetectionResult(
        currentState: FaceSwapState,
        result: DetectionResult,
    ): FaceSwapState {
        val guide = when {
            result.personFace != null && result.dogBox != null -> GuideMessage.Ready
            result.personFace != null -> GuideMessage.NeedDog
            result.dogBox != null -> GuideMessage.NeedPerson
            else -> GuideMessage.WaitingForBoth
        }

        val bitmap = result.frameBitmap
        var personCrop: Bitmap? = null
        var dogCrop: Bitmap? = null
        var dogFaceBox = result.dogBox

        val face = result.personFace
        val dog = result.dogBox
        if (bitmap != null && face != null && dog != null) {
            personCrop = cropNormalized(bitmap, face.boundingBox)
            val faceRect = estimateDogFaceRect(dog.rect)
            dogCrop = cropPixel(bitmap, faceRect)
            dogFaceBox = dog.copy(rect = faceRect)
        }

        return currentState.copy(
            personFace = result.personFace,
            dogBoundingBox = dogFaceBox,
            guideMessage = guide,
            isSwapEnabled = guide == GuideMessage.Ready,
            personCrop = personCrop,
            dogCrop = dogCrop,
            frameWidth = bitmap?.width ?: currentState.frameWidth,
            frameHeight = bitmap?.height ?: currentState.frameHeight,
        )
    }

    private fun cropNormalized(bitmap: Bitmap, normalizedRect: RectF): Bitmap? {
        val w = bitmap.width
        val h = bitmap.height
        val left = max(0, (normalizedRect.left * w).toInt())
        val top = max(0, (normalizedRect.top * h).toInt())
        val right = min(w, (normalizedRect.right * w).toInt())
        val bottom = min(h, (normalizedRect.bottom * h).toInt())
        val cropW = right - left
        val cropH = bottom - top
        if (cropW <= 0 || cropH <= 0) return null
        return Bitmap.createBitmap(bitmap, left, top, cropW, cropH)
    }

    private fun estimateDogFaceRect(bodyRect: RectF): RectF {
        val bboxW = bodyRect.width()
        val bboxH = bodyRect.height()
        val aspectRatio = bboxW / bboxH.coerceAtLeast(1f)
        val cx = bodyRect.centerX()

        return when {
            aspectRatio > 1.4f -> {
                // 누워있는 강아지: 머리가 좌우 한쪽 끝에 있음. 왼쪽 끝 우선
                val faceW = bboxW * 0.35f
                val faceH = bboxH * 0.90f
                RectF(bodyRect.left, bodyRect.top, bodyRect.left + faceW, bodyRect.top + faceH)
            }
            aspectRatio > 0.7f -> {
                // 정면/앉은 강아지
                val faceW = bboxW * 0.72f
                val faceH = bboxH * 0.58f
                RectF(cx - faceW / 2f, bodyRect.top, cx + faceW / 2f, bodyRect.top + faceH)
            }
            else -> {
                // 서있는 강아지: 머리가 전신의 상단 일부
                val faceW = bboxW * 0.72f
                val faceH = bboxH * 0.38f
                RectF(cx - faceW / 2f, bodyRect.top, cx + faceW / 2f, bodyRect.top + faceH)
            }
        }
    }

    private fun cropPixel(bitmap: Bitmap, pixelRect: RectF): Bitmap? {
        val w = bitmap.width
        val h = bitmap.height
        val left = max(0, pixelRect.left.toInt())
        val top = max(0, pixelRect.top.toInt())
        val right = min(w, pixelRect.right.toInt())
        val bottom = min(h, pixelRect.bottom.toInt())
        val cropW = right - left
        val cropH = bottom - top
        if (cropW <= 0 || cropH <= 0) return null
        return Bitmap.createBitmap(bitmap, left, top, cropW, cropH)
    }
}

package com.kero.face.core.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.kero.face.core.model.BoundingBox
import com.kero.face.core.model.FaceLandmarks
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun DetectionOverlay(
    personFace: FaceLandmarks?,
    dogBoundingBox: BoundingBox?,
    personCrop: Bitmap?,
    dogCrop: Bitmap?,
    imageWidth: Int,
    imageHeight: Int,
    isMirrored: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val scaleX = size.width / imageWidth
        val scaleY = size.height / imageHeight

        val dashedStroke = Stroke(
            width = 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 8f)),
        )

        val isSwapping = personCrop != null && dogCrop != null
                && personFace != null && dogBoundingBox != null

        if (isSwapping) {
            // 사람 얼굴 위치에 강아지 크롭을 그림 (정규화 좌표 [0,1] → 스크린 픽셀)
            val faceBox = personFace!!.boundingBox
            val fLeft = if (isMirrored) size.width * (1f - faceBox.right) else faceBox.left * size.width
            val fRight = if (isMirrored) size.width * (1f - faceBox.left) else faceBox.right * size.width
            val fTop = faceBox.top * size.height
            val fBottom = faceBox.bottom * size.height

            drawImage(
                image = dogCrop!!.asImageBitmap(),
                dstOffset = IntOffset(fLeft.roundToInt(), fTop.roundToInt()),
                dstSize = IntSize((fRight - fLeft).roundToInt(), (fBottom - fTop).roundToInt()),
            )

            // 강아지 위치에 사람 얼굴 크롭을 그림
            val dogBox = dogBoundingBox!!.rect
            val dLeft = if (isMirrored) size.width - dogBox.right * scaleX else dogBox.left * scaleX
            val dRight = if (isMirrored) size.width - dogBox.left * scaleX else dogBox.right * scaleX
            val dTop = dogBox.top * scaleY
            val dBottom = dogBox.bottom * scaleY

            drawImage(
                image = personCrop!!.asImageBitmap(),
                dstOffset = IntOffset(dLeft.roundToInt(), dTop.roundToInt()),
                dstSize = IntSize((dRight - dLeft).roundToInt(), (dBottom - dTop).roundToInt()),
            )
        } else {
            // 교환 불가 — 바운딩 박스만 표시
            if (personFace != null) {
                // 정규화 좌표 [0,1] → 스크린 픽셀
                val box = personFace.boundingBox
                val left = if (isMirrored) size.width * (1f - box.right) else box.left * size.width
                val right = if (isMirrored) size.width * (1f - box.left) else box.right * size.width

                drawRect(
                    color = Color(0xFFFF6B9D),
                    topLeft = Offset(left, box.top * size.height),
                    size = Size(right - left, (box.bottom - box.top) * size.height),
                    style = dashedStroke,
                )
            }

            if (dogBoundingBox != null) {
                val box = dogBoundingBox.rect
                val left = if (isMirrored) size.width - box.right * scaleX else box.left * scaleX
                val right = if (isMirrored) size.width - box.left * scaleX else box.right * scaleX

                drawRect(
                    color = Color(0xFF4FC3F7),
                    topLeft = Offset(left, box.top * scaleY),
                    size = Size(right - left, (box.bottom - box.top) * scaleY),
                    style = dashedStroke,
                )
            }
        }
    }
}

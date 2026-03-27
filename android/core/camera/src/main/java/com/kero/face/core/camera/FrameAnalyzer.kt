package com.kero.face.core.camera

import java.nio.ByteBuffer

data class FrameData(
    val buffer: ByteBuffer,
    val width: Int,
    val height: Int,
    val timestampMs: Long,
)

fun interface FrameAnalyzer {
    fun analyze(frameData: FrameData)
}

package com.kero.face.core.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.kero.face.core.camera.internal.AnalysisExecutor

class CameraManager(private val context: Context) {

    private var cameraProvider: androidx.camera.lifecycle.ProcessCameraProvider? = null
    private val analysisExecutor = AnalysisExecutor()

    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        frameAnalyzer: FrameAnalyzer,
        lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    ) {
        val provider = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context).get()
        cameraProvider = provider

        val preview = Preview.Builder()
            .build()
            .also { it.surfaceProvider = surfaceProvider }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(analysisExecutor.executor) { imageProxy ->
                    val buffer = imageProxy.planes[0].buffer
                    val frameData = FrameData(
                        buffer = buffer,
                        width = imageProxy.width,
                        height = imageProxy.height,
                        timestampMs = System.currentTimeMillis(),
                    )
                    frameAnalyzer.analyze(frameData)
                    imageProxy.close()
                }
            }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis,
        )
    }

    fun unbind() {
        cameraProvider?.unbindAll()
    }

    fun shutdown() {
        unbind()
        analysisExecutor.shutdown()
    }
}

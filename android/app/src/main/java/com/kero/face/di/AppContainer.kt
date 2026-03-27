package com.kero.face.di

import android.content.Context
import com.kero.face.core.camera.CameraManager
import com.kero.face.core.ml.DetectionEngine

class AppContainer(private val context: Context) {

    fun createCameraManager(): CameraManager = CameraManager(context)

    fun createDetectionEngine(): DetectionEngine = DetectionEngine.create(context)
}

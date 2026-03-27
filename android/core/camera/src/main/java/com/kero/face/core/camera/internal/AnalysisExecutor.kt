package com.kero.face.core.camera.internal

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class AnalysisExecutor {

    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun shutdown() {
        executor.shutdown()
    }
}

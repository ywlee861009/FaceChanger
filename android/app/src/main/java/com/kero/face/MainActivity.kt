package com.kero.face

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.core.ui.theme.FaceChangerTheme
import com.kero.face.feature.faceswap.FaceSwapViewModel
import com.kero.face.feature.faceswap.ui.FaceSwapScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as FaceChangerApp).container

        setContent {
            FaceChangerTheme {
                val cameraManager = remember { container.createCameraManager() }
                val detectionEngine = remember { container.createDetectionEngine() }
                val viewModel: FaceSwapViewModel = viewModel()

                Surface(modifier = Modifier.fillMaxSize()) {
                    FaceSwapScreen(
                        viewModel = viewModel,
                        cameraManager = cameraManager,
                        detectionEngine = detectionEngine,
                    )
                }
            }
        }
    }
}

package com.kero.face.feature.faceswap.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kero.face.core.camera.CameraManager
import com.kero.face.core.ml.DetectionEngine
import com.kero.face.core.ui.component.CameraPermissionScreen
import com.kero.face.core.ui.component.DetectionOverlay
import com.kero.face.core.ui.component.GuideCard
import com.kero.face.feature.faceswap.FaceSwapEffect
import com.kero.face.feature.faceswap.FaceSwapIntent
import com.kero.face.feature.faceswap.FaceSwapViewModel
import com.kero.face.feature.faceswap.GuideMessage

@Composable
fun FaceSwapScreen(
    viewModel: FaceSwapViewModel,
    cameraManager: CameraManager,
    detectionEngine: DetectionEngine,
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FaceSwapEffect.NavigateToResult -> onNavigateToResult()
                FaceSwapEffect.NavigateBack -> onNavigateBack()
                is FaceSwapEffect.ShowToast -> { /* TODO: 토스트 */ }
            }
        }
    }

    if (!hasCameraPermission) {
        CameraPermissionScreen(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = modifier,
        )
    } else {
        CameraContent(
            viewModel = viewModel,
            cameraManager = cameraManager,
            detectionEngine = detectionEngine,
            onNavigateBack = onNavigateBack,
            modifier = modifier,
        )
    }
}

@Composable
private fun CameraContent(
    viewModel: FaceSwapViewModel,
    cameraManager: CameraManager,
    detectionEngine: DetectionEngine,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var imageWidth by remember { mutableIntStateOf(640) }
    var imageHeight by remember { mutableIntStateOf(480) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(Unit) {
        detectionEngine.initialize()
    }

    DisposableEffect(lifecycleOwner) {
        cameraManager.bindCamera(
            lifecycleOwner = lifecycleOwner,
            surfaceProvider = previewView.surfaceProvider,
            frameAnalyzer = { frameData ->
                val rotated = frameData.rotationDegrees == 90 || frameData.rotationDegrees == 270
                imageWidth = if (rotated) frameData.height else frameData.width
                imageHeight = if (rotated) frameData.width else frameData.height

                detectionEngine.processFrame(
                    frameBuffer = frameData.buffer,
                    width = frameData.width,
                    height = frameData.height,
                    timestampMs = frameData.timestampMs,
                    rotationDegrees = frameData.rotationDegrees,
                ) { result ->
                    viewModel.dispatch(FaceSwapIntent.OnFrameAnalyzed(result))
                }
            },
        )
        viewModel.dispatch(FaceSwapIntent.StartCamera)

        onDispose {
            cameraManager.shutdown()
            detectionEngine.close()
            viewModel.dispatch(FaceSwapIntent.StopCamera)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
        )

        DetectionOverlay(
            personFace = state.personFace,
            dogBoundingBox = state.dogBoundingBox,
            personCrop = state.personCrop,
            dogCrop = state.dogCrop,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            isMirrored = true,
            modifier = Modifier.fillMaxSize(),
        )

        val guideText = when (state.guideMessage) {
            GuideMessage.WaitingForBoth -> "사람과 강아지를 함께 비춰주세요"
            GuideMessage.NeedDog -> "강아지도 같이 비춰주세요!"
            GuideMessage.NeedPerson -> "사람 얼굴도 필요해요!"
            GuideMessage.Ready -> null
        }

        if (guideText != null) {
            GuideCard(
                message = guideText,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
            )
        }

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로",
            )
        }

        FloatingActionButton(
            onClick = { viewModel.dispatch(FaceSwapIntent.Capture) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
        ) {
            Text("촬영")
        }
    }
}

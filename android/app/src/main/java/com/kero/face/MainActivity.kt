package com.kero.face

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.core.ui.theme.FaceChangerTheme
import com.kero.face.feature.faceswap.FaceSwapViewModel
import com.kero.face.feature.faceswap.ui.FaceSwapScreen
import com.kero.face.feature.home.ui.HomeScreen
import com.kero.face.feature.photoswap.ui.PhotoSwapScreen
import com.kero.face.feature.result.ui.ResultScreen
import com.kero.face.feature.splash.ui.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as FaceChangerApp).container

        setContent {
            FaceChangerTheme {
                // 화면 스택 — 마지막 항목이 현재 화면
                var screenStack by remember {
                    mutableStateOf(listOf<AppScreen>(AppScreen.Splash))
                }
                val currentScreen = screenStack.last()

                fun push(screen: AppScreen) {
                    screenStack = screenStack + screen
                }

                fun pop() {
                    if (screenStack.size > 1) screenStack = screenStack.dropLast(1)
                }

                fun replaceTop(screen: AppScreen) {
                    screenStack = screenStack.dropLast(1) + screen
                }

                // 시스템 뒤로가기: Splash/Home은 앱 종료, 나머지는 스택 pop
                BackHandler(enabled = screenStack.size > 1) {
                    pop()
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val screen = currentScreen) {
                        AppScreen.Splash -> {
                            SplashScreen(
                                onNavigateToHome = { replaceTop(AppScreen.Home) },
                            )
                        }

                        AppScreen.Home -> {
                            HomeScreen(
                                onNavigateToLiveCamera = { push(AppScreen.LiveCamera) },
                                onNavigateToPhotoSwap = { push(AppScreen.PhotoSwap) },
                            )
                        }

                        AppScreen.LiveCamera -> {
                            val cameraManager = remember { container.createCameraManager() }
                            val detectionEngine = remember { container.createDetectionEngine() }
                            val faceSwapViewModel: FaceSwapViewModel = viewModel()

                            FaceSwapScreen(
                                viewModel = faceSwapViewModel,
                                cameraManager = cameraManager,
                                detectionEngine = detectionEngine,
                                onNavigateToResult = { push(AppScreen.Result(ResultSource.LiveCamera)) },
                                onNavigateBack = { pop() },
                            )
                        }

                        AppScreen.PhotoSwap -> {
                            PhotoSwapScreen(
                                onNavigateToResult = { push(AppScreen.Result(ResultSource.PhotoSwap)) },
                                onNavigateBack = { pop() },
                            )
                        }

                        is AppScreen.Result -> {
                            val retryLabel = when (screen.source) {
                                ResultSource.LiveCamera -> "다시 찍기"
                                ResultSource.PhotoSwap -> "다시 하기"
                            }

                            ResultScreen(
                                retryLabel = retryLabel,
                                onNavigateBack = { pop() },
                                onShare = {
                                    // TODO: 실제 공유 데이터 전달
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/*"
                                    }
                                    startActivity(Intent.createChooser(shareIntent, "공유하기"))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

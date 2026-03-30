package com.kero.face

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.core.ui.theme.FaceChangerTheme
import com.kero.face.feature.faceswap.FaceSwapViewModel
import com.kero.face.feature.faceswap.ui.FaceSwapScreen
import com.kero.face.feature.gallery.ui.GalleryScreen
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

                var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
                var pendingSwappedBitmap by remember { mutableStateOf<Bitmap?>(null) }
                var lastBackPressTime by remember { mutableLongStateOf(0L) }

                // Home 화면에서 뒤로가기 두 번 → 앱 종료
                BackHandler(enabled = currentScreen == AppScreen.Home) {
                    val now = System.currentTimeMillis()
                    if (now - lastBackPressTime < 2000L) {
                        finish()
                    } else {
                        lastBackPressTime = now
                        Toast.makeText(this@MainActivity, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                    }
                }

                // 시스템 뒤로가기: 나머지는 스택 pop
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
                                onNavigateToPhotoSwap = { push(AppScreen.Gallery) },
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

                        AppScreen.Gallery -> {
                            GalleryScreen(
                                onPhotoSelected = { uri ->
                                    pendingPhotoUri = uri
                                    push(AppScreen.PhotoSwap)
                                },
                                onNavigateBack = { pop() },
                            )
                        }

                        AppScreen.PhotoSwap -> {
                            PhotoSwapScreen(
                                initialPhotoUri = pendingPhotoUri,
                                onNavigateToResult = { bitmap ->
                                    pendingSwappedBitmap = bitmap
                                    push(AppScreen.Result(ResultSource.PhotoSwap))
                                },
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
                                swappedBitmap = pendingSwappedBitmap,
                                onNavigateBack = { pop() },
                                onShare = { uri ->
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/*"
                                        if (uri != null) {
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
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

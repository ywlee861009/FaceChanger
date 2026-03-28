package com.kero.face.feature.photoswap.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.core.model.DetectionResult
import com.kero.face.feature.photoswap.PhotoSwapEffect
import com.kero.face.feature.photoswap.PhotoSwapIntent
import com.kero.face.feature.photoswap.PhotoSwapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSwapScreen(
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoSwapViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.dispatch(PhotoSwapIntent.PhotoSelected(uri))
        } else {
            viewModel.dispatch(PhotoSwapIntent.PickerCancelled)
        }
    }

    // 화면 진입 시 자동으로 이미지 피커 실행
    LaunchedEffect(Unit) {
        imagePicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PhotoSwapEffect.NavigateBack -> onNavigateBack()
                is PhotoSwapEffect.ShowError -> { /* TODO: 스낵바 */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진으로 얼굴 바꾸기") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.dispatch(PhotoSwapIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val bitmap = state.bitmap
        val detectionResult = state.detectionResult

        when {
                state.isAnalyzing -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            CircularProgressIndicator()
                            Text("얼굴을 분석하는 중...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                bitmap != null -> {
                    ImageWithOverlay(
                        bitmap = bitmap,
                        detectionResult = detectionResult,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )

                    DetectionStatusRow(detectionResult = state.detectionResult)

                    // 디버그 패널
                    val debugText = state.debugInfo ?: state.error
                    if (debugText != null) {
                        DebugPanel(text = debugText, isError = state.error != null)
                    }

                    Button(
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("사진 다시 선택")
                    }
                }

                else -> {
                    // 이미지 미선택 상태 (피커가 닫혔을 때)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.large,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("사진을 선택해주세요", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "강아지와 사람이 함께 있는 사진",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Button(
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("사진 선택")
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageWithOverlay(
    bitmap: Bitmap,
    detectionResult: DetectionResult?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "선택된 사진",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )

        if (detectionResult != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height
                val bitmapW = bitmap.width.toFloat()
                val bitmapH = bitmap.height.toFloat()

                // ContentScale.Fit 에 맞춰 실제 이미지가 그려지는 영역 계산
                val bitmapAspect = bitmapW / bitmapH
                val canvasAspect = canvasW / canvasH
                val displayW: Float
                val displayH: Float
                val offsetX: Float
                val offsetY: Float
                if (bitmapAspect > canvasAspect) {
                    displayW = canvasW
                    displayH = canvasW / bitmapAspect
                    offsetX = 0f
                    offsetY = (canvasH - displayH) / 2f
                } else {
                    displayH = canvasH
                    displayW = canvasH * bitmapAspect
                    offsetX = (canvasW - displayW) / 2f
                    offsetY = 0f
                }

                // 사람 얼굴 원 (파란색) — 정규화된 [0,1] 좌표
                detectionResult.personFace?.boundingBox?.let { box ->
                    val cx = offsetX + (box.left + box.right) / 2f * displayW
                    val cy = offsetY + (box.top + box.bottom) / 2f * displayH
                    val rx = box.width() / 2f * displayW
                    val ry = box.height() / 2f * displayH
                    drawCircle(
                        color = Color(0xFF2196F3),
                        radius = (rx + ry) / 2f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 5f),
                    )
                }

                // 강아지 원 (주황색) — 픽셀 좌표
                detectionResult.dogBox?.rect?.let { box ->
                    val cx = offsetX + (box.left + box.right) / 2f / bitmapW * displayW
                    val cy = offsetY + (box.top + box.bottom) / 2f / bitmapH * displayH
                    val rx = box.width() / 2f / bitmapW * displayW
                    val ry = box.height() / 2f / bitmapH * displayH
                    drawCircle(
                        color = Color(0xFFFF9800),
                        radius = (rx + ry) / 2f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 5f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetectionStatusRow(detectionResult: DetectionResult?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DetectionChip(
            label = "사람 얼굴",
            detected = detectionResult?.personFace != null,
            color = Color(0xFF2196F3),
        )
        DetectionChip(
            label = "강아지",
            detected = detectionResult?.dogBox != null,
            color = Color(0xFFFF9800),
        )
    }
}

@Composable
private fun DebugPanel(text: String, isError: Boolean) {
    val bgColor = if (isError) Color(0xFFFFEBEB) else Color(0xFF1A1A2E)
    val borderColor = if (isError) Color(0xFFE53935) else Color(0xFF00BCD4)
    val textColor = if (isError) Color(0xFFB71C1C) else Color(0xFFE0F7FA)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = androidx.compose.ui.unit.TextUnit(13f, androidx.compose.ui.unit.TextUnitType.Sp),
                lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp),
            ),
            color = textColor,
        )
    }
}

@Composable
private fun DetectionChip(
    label: String,
    detected: Boolean,
    color: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = if (detected) color else MaterialTheme.colorScheme.outline,
                    shape = CircleShape,
                ),
        )
        Text(
            text = if (detected) "$label 감지됨" else "$label 미감지",
            style = MaterialTheme.typography.labelSmall,
            color = if (detected) color else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

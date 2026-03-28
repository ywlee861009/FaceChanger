package com.kero.face.feature.splash.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.core.ui.theme.FcTheme
import com.kero.face.feature.splash.SplashEffect
import com.kero.face.feature.splash.SplashIntent
import com.kero.face.feature.splash.SplashPhase
import com.kero.face.feature.splash.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    SplashContent(
        phase = state.phase,
        onPhase1Done = { viewModel.dispatch(SplashIntent.AnimationPhase1Done) },
        onPhase2Done = { viewModel.dispatch(SplashIntent.AnimationPhase2Done) },
    )
}

@Composable
private fun SplashContent(
    phase: SplashPhase,
    onPhase1Done: () -> Unit,
    onPhase2Done: () -> Unit,
) {
    val colors = FcTheme.colors

    val swapProgress = remember { Animatable(0f) }
    val facesAlpha = remember { Animatable(1f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.5f) }
    var showDots by remember { mutableStateOf(true) }

    // Phase 1: 얼굴 교환 애니메이션 (0ms ~ 1000ms)
    LaunchedEffect(Unit) {
        swapProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        )
        onPhase1Done()
    }

    // Phase 2: 타이틀 등장 + 얼굴 페이드아웃 (1000ms ~ 1500ms)
    LaunchedEffect(phase) {
        if (phase == SplashPhase.TITLE_IN) {
            showDots = false
            launch { facesAlpha.animateTo(0f, tween(500, easing = LinearOutSlowInEasing)) }
            launch { titleAlpha.animateTo(1f, tween(500, easing = LinearOutSlowInEasing)) }
            titleScale.animateTo(1f, tween(500, easing = LinearOutSlowInEasing))
            delay(50L) // 마지막 프레임 안정화
            onPhase2Done()
        }
    }

    val density = LocalDensity.current
    val iconSizeDp: Dp = 72.dp
    val gapDp: Dp = 24.dp
    val archHeightDp: Dp = 20.dp

    val swapDistancePx: Float
    val archHeightPx: Float
    with(density) {
        swapDistancePx = (iconSizeDp + gapDp).toPx()
        archHeightPx = archHeightDp.toPx()
    }

    val swapProg = swapProgress.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to colors.background,
                        0.6f to colors.background,
                        1.0f to colors.primaryContainer.copy(alpha = 0.25f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        // 배경 발자국 장식
        SplashPawDecorations()

        // 얼굴 아이콘 영역
        Box(
            modifier = Modifier.graphicsLayer { alpha = facesAlpha.value },
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(gapDp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 사람 얼굴 아이콘 (왼쪽 → 오른쪽, 위로 아치)
                FaceIconCircle(
                    modifier = Modifier
                        .size(iconSizeDp)
                        .graphicsLayer {
                            translationX = swapProg * swapDistancePx
                            translationY = -sin(swapProg * PI.toFloat()) * archHeightPx
                        },
                    backgroundColor = colors.primary,
                ) {
                    PersonIconCanvas(
                        modifier = Modifier.size(36.dp),
                        color = colors.onPrimary,
                    )
                }

                // 강아지 얼굴 아이콘 (오른쪽 → 왼쪽, 아래로 아치)
                FaceIconCircle(
                    modifier = Modifier
                        .size(iconSizeDp)
                        .graphicsLayer {
                            translationX = -swapProg * swapDistancePx
                            translationY = sin(swapProg * PI.toFloat()) * archHeightPx
                        },
                    backgroundColor = colors.secondary,
                ) {
                    DogPawIconCanvas(
                        modifier = Modifier.size(36.dp),
                        color = colors.onPrimary,
                    )
                }
            }
        }

        // 앱 타이틀 (Phase 2에서 등장)
        Text(
            text = "FaceChanger",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            modifier = Modifier.graphicsLayer {
                alpha = titleAlpha.value
                scaleX = titleScale.value
                scaleY = titleScale.value
            },
        )

        // 로딩 인디케이터 (Phase 1에서만 표시)
        if (showDots) {
            BouncingDots(
                modifier = Modifier.offset(y = 120.dp),
                color = colors.primary,
            )
        }
    }
}

@Composable
private fun FaceIconCircle(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun PersonIconCanvas(
    modifier: Modifier = Modifier,
    color: Color,
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawPersonSilhouette(color)
    }
}

private fun DrawScope.drawPersonSilhouette(color: Color) {
    val w = size.width
    val h = size.height

    // 머리 (원)
    drawCircle(
        color = color,
        radius = w * 0.23f,
        center = Offset(w * 0.5f, h * 0.27f),
    )

    // 몸통 (타원 상단)
    val bodyPath = Path().apply {
        addOval(
            Rect(
                left = w * 0.06f,
                top = h * 0.5f,
                right = w * 0.94f,
                bottom = h * 1.3f,
            ),
        )
    }
    drawPath(bodyPath, color = color)
}

@Composable
private fun DogPawIconCanvas(
    modifier: Modifier = Modifier,
    color: Color,
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawDogPaw(color)
    }
}

private fun DrawScope.drawDogPaw(color: Color) {
    val w = size.width
    val h = size.height

    // 메인 발바닥 패드
    drawCircle(
        color = color,
        radius = w * 0.28f,
        center = Offset(w * 0.5f, h * 0.68f),
    )
    // 발가락 패드 4개
    val toeR = w * 0.16f
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.18f, h * 0.36f))
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.38f, h * 0.18f))
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.62f, h * 0.18f))
    drawCircle(color = color, radius = toeR, center = Offset(w * 0.82f, h * 0.36f))
}

@Composable
private fun BouncingDots(
    modifier: Modifier = Modifier,
    color: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_dots")

    val dot1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                0f at 0
                -10f at 150
                0f at 300
                0f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot1Y",
    )
    val dot2Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                0f at 100
                -10f at 250
                0f at 400
                0f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot2Y",
    )
    val dot3Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                0f at 200
                -10f at 350
                0f at 500
                0f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot3Y",
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BouncingDot(color = color, yOffsetPx = dot1Y)
        BouncingDot(color = color, yOffsetPx = dot2Y)
        BouncingDot(color = color, yOffsetPx = dot3Y)
    }
}

@Composable
private fun BouncingDot(color: Color, yOffsetPx: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { translationY = yOffsetPx }
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun SplashPawDecorations() {
    val colors = FcTheme.colors
    val pawColor = colors.primary.copy(alpha = 0.12f)

    // 배경 발자국 장식 (고정 위치)
    Box(modifier = Modifier.fillMaxSize()) {
        SmallPawDecoration(
            modifier = Modifier
                .offset(x = 32.dp, y = 80.dp)
                .size(22.dp),
            color = pawColor,
            rotation = -30f,
        )
        SmallPawDecoration(
            modifier = Modifier
                .offset(x = 280.dp, y = 60.dp)
                .size(18.dp),
            color = colors.secondary.copy(alpha = 0.15f),
            rotation = 25f,
        )
        SmallPawDecoration(
            modifier = Modifier
                .offset(x = 20.dp, y = 480.dp)
                .size(20.dp),
            color = colors.secondary.copy(alpha = 0.12f),
            rotation = 15f,
        )
        SmallPawDecoration(
            modifier = Modifier
                .offset(x = 310.dp, y = 500.dp)
                .size(16.dp),
            color = pawColor,
            rotation = -20f,
        )
        SmallPawDecoration(
            modifier = Modifier
                .offset(x = 150.dp, y = 700.dp)
                .size(14.dp),
            color = pawColor,
            rotation = 10f,
        )
    }
}

@Composable
private fun SmallPawDecoration(
    modifier: Modifier = Modifier,
    color: Color,
    rotation: Float = 0f,
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier.graphicsLayer { rotationZ = rotation },
    ) {
        drawDogPaw(color)
    }
}

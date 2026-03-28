package com.kero.face.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.feature.home.HomeEffect
import com.kero.face.feature.home.HomeIntent
import com.kero.face.feature.home.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToLiveCamera: () -> Unit,
    onNavigateToPhotoSwap: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToLiveCamera -> onNavigateToLiveCamera()
                HomeEffect.NavigateToPhotoSwap -> onNavigateToPhotoSwap()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "FaceChanger",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "사람과 강아지의 얼굴을 바꿔보세요!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = { viewModel.dispatch(HomeIntent.ClickLiveCamera) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("실시간 변경")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.dispatch(HomeIntent.ClickPhotoSwap) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("사진 변경")
        }
    }
}

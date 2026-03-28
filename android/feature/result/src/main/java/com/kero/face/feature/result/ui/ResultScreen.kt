package com.kero.face.feature.result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.feature.result.ResultEffect
import com.kero.face.feature.result.ResultIntent
import com.kero.face.feature.result.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    retryLabel: String,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ResultEffect.NavigateBack -> onNavigateBack()
                ResultEffect.ShareResult -> onShare()
                is ResultEffect.ShowMessage -> { /* TODO: 스낵바 */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("결과") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.dispatch(ResultIntent.Retry) }) {
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
                .padding(24.dp),
        ) {
            // TODO: 결과 이미지 표시
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
                Text(
                    text = "교환된 이미지",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { viewModel.dispatch(ResultIntent.Retry) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(retryLabel)
                }

                Button(
                    onClick = { viewModel.dispatch(ResultIntent.Save) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving,
                ) {
                    Text("저장")
                }

                Button(
                    onClick = { viewModel.dispatch(ResultIntent.Share) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("공유")
                }
            }
        }
    }
}

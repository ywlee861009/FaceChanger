package com.kero.face.feature.photoswap.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val hasMediaPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PhotoSwapEffect.NavigateToResult -> onNavigateToResult()
                PhotoSwapEffect.NavigateBack -> onNavigateBack()
                is PhotoSwapEffect.ShowError -> { /* TODO: 스낵바 */ }
            }
        }
    }

    val dogPhotoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.dispatch(PhotoSwapIntent.DogPhotoSelected(it)) }
    }

    val personPhotoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.dispatch(PhotoSwapIntent.PersonPhotoSelected(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진 변경") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PhotoPickerCard(
                    label = "강아지 사진",
                    uri = state.dogPhotoUri,
                    onClick = {
                        dogPhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                PhotoPickerCard(
                    label = "사람 사진",
                    uri = state.personPhotoUri,
                    onClick = {
                        personPhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.dispatch(PhotoSwapIntent.StartSwap) },
                enabled = state.isSwapEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isProcessing) "처리 중..." else "교환하기")
            }
        }
    }
}

@Composable
private fun PhotoPickerCard(
    label: String,
    uri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = if (uri != null) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp),
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (uri == null) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "사진 선택",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                // TODO: 선택된 이미지 표시 (Coil 등)
                Text(
                    text = "선택됨",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

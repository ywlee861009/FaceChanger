package com.kero.face.feature.gallery.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kero.face.core.ui.component.FcSmallButton
import com.kero.face.core.ui.theme.FcTheme
import com.kero.face.feature.gallery.GalleryEffect
import com.kero.face.feature.gallery.GalleryIntent
import com.kero.face.feature.gallery.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onPhotoSelected: (Uri) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.dispatch(GalleryIntent.PermissionResult(granted))
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GalleryEffect.NavigateWithPhoto -> onPhotoSelected(effect.uri)
                GalleryEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진 선택") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.dispatch(GalleryIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    FcSmallButton(
                        text = "선택",
                        onClick = { viewModel.dispatch(GalleryIntent.ConfirmSelection) },
                        enabled = state.selectedPhotoUri != null,
                        modifier = Modifier.padding(end = FcTheme.spacing.sm),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FcTheme.colors.surface,
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        if (!state.permissionGranted) {
            MediaPermissionScreen(
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                },
                modifier = Modifier.padding(innerPadding),
            )
        } else if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = FcTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                AlbumDropdown(
                    selectedAlbumName = state.selectedAlbumName,
                    totalCount = state.totalPhotoCount,
                    albums = state.albums,
                    isExpanded = state.isAlbumDropdownExpanded,
                    onToggle = { viewModel.dispatch(GalleryIntent.ToggleAlbumDropdown) },
                    onSelectAlbum = { viewModel.dispatch(GalleryIntent.SelectAlbum(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                PhotoGrid(
                    photos = state.photoUris,
                    selectedUri = state.selectedPhotoUri,
                    onPhotoClick = { viewModel.dispatch(GalleryIntent.SelectPhoto(it)) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = FcTheme.spacing.xs),
                )
            }
        }
    }
}

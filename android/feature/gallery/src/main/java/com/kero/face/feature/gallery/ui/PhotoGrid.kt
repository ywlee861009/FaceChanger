package com.kero.face.feature.gallery.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kero.face.core.ui.theme.FcTheme

@Composable
internal fun PhotoGrid(
    photos: List<Uri>,
    selectedUri: Uri?,
    onPhotoClick: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FcTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(FcTheme.spacing.xs),
    ) {
        items(photos, key = { it.toString() }) { uri ->
            val isSelected = uri == selectedUri
            PhotoGridItem(
                uri = uri,
                isSelected = isSelected,
                onClick = { onPhotoClick(uri) },
            )
        }
    }
}

@Composable
private fun PhotoGridItem(
    uri: Uri,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(FcTheme.shapes.thumbnail)
            .then(
                if (isSelected) Modifier.border(3.dp, FcTheme.colors.primary, FcTheme.shapes.thumbnail)
                else Modifier
            )
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        if (isSelected) {
            // Semi-transparent overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FcTheme.colors.primary.copy(alpha = 0.2f)),
            )
            // Check icon
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(FcTheme.spacing.xs)
                    .size(24.dp)
                    .background(FcTheme.colors.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "선택됨",
                    modifier = Modifier.size(16.dp),
                    tint = FcTheme.colors.onPrimary,
                )
            }
        }
    }
}

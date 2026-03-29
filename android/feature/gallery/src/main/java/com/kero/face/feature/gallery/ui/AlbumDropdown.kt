package com.kero.face.feature.gallery.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kero.face.core.ui.theme.FcTheme
import com.kero.face.feature.gallery.internal.Album

@Composable
internal fun AlbumDropdown(
    selectedAlbumName: String,
    totalCount: Int,
    albums: List<Album>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSelectAlbum: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(horizontal = FcTheme.spacing.md, vertical = FcTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedAlbumName,
                style = MaterialTheme.typography.titleSmall,
                color = FcTheme.colors.onBackground,
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "접기" else "펼치기",
                tint = FcTheme.colors.onSurfaceVariant,
            )
        }

        // Dropdown list
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FcTheme.spacing.sm)
                    .background(FcTheme.colors.surface, FcTheme.shapes.md)
                    .clip(FcTheme.shapes.md),
            ) {
                // "전체 사진" item
                AlbumItem(
                    name = "전체 사진",
                    count = totalCount,
                    coverUri = albums.firstOrNull()?.coverUri,
                    onClick = { onSelectAlbum(null) },
                )
                albums.forEach { album ->
                    AlbumItem(
                        name = album.displayName,
                        count = album.count,
                        coverUri = album.coverUri,
                        onClick = { onSelectAlbum(album.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumItem(
    name: String,
    count: Int,
    coverUri: Uri?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = FcTheme.spacing.md, vertical = FcTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(FcTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (coverUri != null) {
            AsyncImage(
                model = coverUri,
                contentDescription = name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(FcTheme.shapes.sm),
                contentScale = ContentScale.Crop,
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = FcTheme.colors.onBackground,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = FcTheme.colors.onSurfaceVariant,
        )
    }
}

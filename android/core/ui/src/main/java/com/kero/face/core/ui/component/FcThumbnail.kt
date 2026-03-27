package com.kero.face.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

@Composable
fun FcThumbnail(
    title: String,
    date: String,
    modifier: Modifier = Modifier,
    imageContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier,
        shape = FcTheme.shapes.thumbnail,
        color = FcTheme.colors.surfaceVariant,
        onClick = onClick ?: {},
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(FcTheme.shapes.thumbnail),
            ) {
                if (imageContent != null) {
                    imageContent()
                }
            }
            Column(
                modifier = Modifier.padding(
                    horizontal = FcTheme.spacing.sm,
                    vertical = FcTheme.spacing.sm,
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = FcTheme.colors.onSurface,
                    maxLines = 1,
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = FcTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

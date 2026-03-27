package com.kero.face.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

enum class FcBadgeStyle {
    Primary,
    Secondary,
    Success,
    Error,
    Neutral,
}

@Composable
fun FcBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: FcBadgeStyle = FcBadgeStyle.Primary,
) {
    val colors = FcTheme.colors
    val (containerColor, contentColor) = when (style) {
        FcBadgeStyle.Primary -> colors.primary to colors.onPrimary
        FcBadgeStyle.Secondary -> colors.secondary to colors.onPrimary
        FcBadgeStyle.Success -> colors.success to colors.onBackground
        FcBadgeStyle.Error -> colors.errorContainer to colors.error
        FcBadgeStyle.Neutral -> colors.surfaceVariant to colors.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = containerColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}

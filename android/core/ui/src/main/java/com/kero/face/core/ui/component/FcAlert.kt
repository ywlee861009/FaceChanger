package com.kero.face.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

enum class FcAlertStyle {
    Success,
    Error,
    Info,
    Warning,
}

@Composable
fun FcAlert(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    style: FcAlertStyle = FcAlertStyle.Success,
    icon: ImageVector? = null,
) {
    val colors = FcTheme.colors
    val (containerColor, contentColor) = when (style) {
        FcAlertStyle.Success -> colors.success to colors.onBackground
        FcAlertStyle.Error -> colors.errorContainer to colors.error
        FcAlertStyle.Info -> colors.infoContainer to colors.onBackground
        FcAlertStyle.Warning -> colors.warningContainer to colors.onBackground
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FcTheme.shapes.md,
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor,
                )
                Spacer(modifier = Modifier.width(FcTheme.spacing.sm))
            }
            androidx.compose.foundation.layout.Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = contentColor,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                )
            }
        }
    }
}

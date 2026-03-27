package com.kero.face.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

@Composable
fun FcCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = FcTheme.shapes.card,
        color = FcTheme.colors.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(FcTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(FcTheme.spacing.sm),
        ) {
            content()
        }
    }
}

@Composable
fun FcCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    FcCard(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = FcTheme.colors.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = FcTheme.colors.onSurfaceVariant,
        )
        if (content != null) {
            content()
        }
        if (actionText != null && onAction != null) {
            FcButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

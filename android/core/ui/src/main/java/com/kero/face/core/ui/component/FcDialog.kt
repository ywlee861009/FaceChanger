package com.kero.face.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FcDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Surface(
            shape = FcTheme.shapes.card,
            color = FcTheme.colors.surface,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(FcTheme.spacing.lg),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = FcTheme.colors.onSurface,
                )
                Spacer(modifier = Modifier.height(FcTheme.spacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FcTheme.colors.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(FcTheme.spacing.lg))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FcTheme.spacing.sm, androidx.compose.ui.Alignment.End),
                ) {
                    if (dismissText != null) {
                        FcButton(
                            text = dismissText,
                            onClick = onDismiss,
                            style = FcButtonStyle.Outline,
                        )
                    }
                    FcButton(
                        text = confirmText,
                        onClick = onConfirm,
                    )
                }
            }
        }
    }
}

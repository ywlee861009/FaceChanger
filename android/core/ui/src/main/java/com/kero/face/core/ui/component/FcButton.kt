package com.kero.face.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kero.face.core.ui.theme.FcTheme

enum class FcButtonStyle {
    Primary,
    Secondary,
    Outline,
    Ghost,
    Destructive,
}

@Composable
fun FcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: FcButtonStyle = FcButtonStyle.Primary,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    val colors = FcTheme.colors

    when (style) {
        FcButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = FcTheme.shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            ) {
                ButtonContent(text = text, icon = leadingIcon)
            }
        }

        FcButtonStyle.Secondary -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = FcTheme.shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondary,
                    contentColor = colors.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            ) {
                ButtonContent(text = text, icon = leadingIcon)
            }
        }

        FcButtonStyle.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = FcTheme.shapes.button,
                border = BorderStroke(1.5.dp, colors.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            ) {
                ButtonContent(text = text, icon = leadingIcon)
            }
        }

        FcButtonStyle.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = FcTheme.shapes.button,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colors.onBackground,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            ) {
                ButtonContent(text = text, icon = leadingIcon)
            }
        }

        FcButtonStyle.Destructive -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = FcTheme.shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.error,
                    contentColor = colors.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            ) {
                ButtonContent(text = text, icon = leadingIcon)
            }
        }
    }
}

@Composable
fun FcSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = FcTheme.shapes.pill,
        colors = ButtonDefaults.buttonColors(
            containerColor = FcTheme.colors.primary,
            contentColor = FcTheme.colors.onPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(text = text)
    }
}

@Composable
fun FcIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FcTheme.colors.primary,
    contentColor: Color = FcTheme.colors.onPrimary,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
) {
    if (icon != null) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
    }
    Text(text = text)
}

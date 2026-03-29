package com.kero.face.feature.gallery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kero.face.core.ui.component.FcButton
import com.kero.face.core.ui.theme.FcTheme

@Composable
internal fun MediaPermissionScreen(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "사진 접근 권한이 필요합니다",
            style = MaterialTheme.typography.titleMedium,
            color = FcTheme.colors.onBackground,
        )
        Spacer(modifier = Modifier.height(FcTheme.spacing.md))
        Text(
            text = "갤러리에서 사진을 선택하려면\n사진 접근을 허용해주세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = FcTheme.colors.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(FcTheme.spacing.lg))
        FcButton(
            text = "권한 허용하기",
            onClick = onRequestPermission,
        )
    }
}

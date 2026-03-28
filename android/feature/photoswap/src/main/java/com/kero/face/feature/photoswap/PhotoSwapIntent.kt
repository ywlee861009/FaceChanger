package com.kero.face.feature.photoswap

import android.net.Uri

sealed interface PhotoSwapIntent {
    data class PhotoSelected(val uri: Uri) : PhotoSwapIntent
    data object NavigateBack : PhotoSwapIntent
}

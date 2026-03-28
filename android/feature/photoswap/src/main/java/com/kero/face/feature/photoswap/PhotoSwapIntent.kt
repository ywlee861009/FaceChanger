package com.kero.face.feature.photoswap

import android.net.Uri

sealed interface PhotoSwapIntent {
    data class DogPhotoSelected(val uri: Uri) : PhotoSwapIntent
    data class PersonPhotoSelected(val uri: Uri) : PhotoSwapIntent
    data object StartSwap : PhotoSwapIntent
    data object NavigateBack : PhotoSwapIntent
}

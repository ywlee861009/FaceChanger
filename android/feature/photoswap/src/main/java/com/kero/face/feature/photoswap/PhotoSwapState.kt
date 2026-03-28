package com.kero.face.feature.photoswap

import android.net.Uri

data class PhotoSwapState(
    val dogPhotoUri: Uri? = null,
    val personPhotoUri: Uri? = null,
    val isProcessing: Boolean = false,
    val error: String? = null,
) {
    val isSwapEnabled: Boolean get() = dogPhotoUri != null && personPhotoUri != null && !isProcessing
}

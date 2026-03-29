package com.kero.face.feature.gallery

import android.net.Uri

sealed interface GalleryEffect {
    data class NavigateWithPhoto(val uri: Uri) : GalleryEffect
    data object NavigateBack : GalleryEffect
}

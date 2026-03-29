package com.kero.face.feature.gallery

import android.net.Uri

sealed interface GalleryIntent {
    data object LoadMedia : GalleryIntent
    data class PermissionResult(val granted: Boolean) : GalleryIntent
    data class SelectAlbum(val albumId: Long?) : GalleryIntent
    data class SelectPhoto(val uri: Uri) : GalleryIntent
    data object ToggleAlbumDropdown : GalleryIntent
    data object NavigateBack : GalleryIntent
    data object ConfirmSelection : GalleryIntent
}

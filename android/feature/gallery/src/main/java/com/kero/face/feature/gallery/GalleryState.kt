package com.kero.face.feature.gallery

import android.net.Uri
import com.kero.face.feature.gallery.internal.Album

data class GalleryState(
    val permissionGranted: Boolean = false,
    val albums: List<Album> = emptyList(),
    val selectedAlbumId: Long? = null,
    val photoUris: List<Uri> = emptyList(),
    val selectedPhotoUri: Uri? = null,
    val isLoading: Boolean = false,
    val isAlbumDropdownExpanded: Boolean = false,
) {
    val selectedAlbumName: String
        get() = if (selectedAlbumId == null) "전체 사진"
        else albums.find { it.id == selectedAlbumId }?.displayName ?: "전체 사진"

    val totalPhotoCount: Int
        get() = albums.sumOf { it.count }
}

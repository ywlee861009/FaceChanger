package com.kero.face.feature.gallery.internal

import android.net.Uri

internal data class MediaImage(
    val id: Long,
    val uri: Uri,
    val bucketId: Long,
    val bucketDisplayName: String,
    val dateAdded: Long,
)

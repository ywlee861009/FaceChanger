package com.kero.face.feature.gallery.internal

import android.net.Uri

data class Album(
    val id: Long,
    val displayName: String,
    val coverUri: Uri,
    val count: Int,
)

package com.kero.face.feature.result

import android.graphics.Bitmap
import android.net.Uri

data class ResultState(
    val bitmap: Bitmap? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val savedUri: Uri? = null,
    val error: String? = null,
)

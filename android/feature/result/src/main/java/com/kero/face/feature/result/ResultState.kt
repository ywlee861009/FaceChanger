package com.kero.face.feature.result

data class ResultState(
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
)

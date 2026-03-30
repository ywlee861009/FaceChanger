package com.kero.face.feature.result

import android.net.Uri

sealed interface ResultEffect {
    data object NavigateBack : ResultEffect
    data class ShareResult(val uri: Uri?) : ResultEffect
    data class ShowMessage(val message: String) : ResultEffect
}

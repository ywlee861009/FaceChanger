package com.kero.face.feature.result

sealed interface ResultEffect {
    data object NavigateBack : ResultEffect
    data object ShareResult : ResultEffect
    data class ShowMessage(val message: String) : ResultEffect
}

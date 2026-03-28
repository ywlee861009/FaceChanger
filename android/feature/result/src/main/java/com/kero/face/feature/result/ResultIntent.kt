package com.kero.face.feature.result

sealed interface ResultIntent {
    data object Save : ResultIntent
    data object Share : ResultIntent
    data object Retry : ResultIntent
}

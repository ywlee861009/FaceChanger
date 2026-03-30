package com.kero.face.feature.result

import android.graphics.Bitmap

sealed interface ResultIntent {
    data class Initialize(val bitmap: Bitmap) : ResultIntent
    data object Save : ResultIntent
    data object Share : ResultIntent
    data object Retry : ResultIntent
}

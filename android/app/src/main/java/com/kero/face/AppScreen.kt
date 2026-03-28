package com.kero.face

sealed class AppScreen {
    data object Splash : AppScreen()
    data object Home : AppScreen()
    data object LiveCamera : AppScreen()
    data object PhotoSwap : AppScreen()
    data class Result(val source: ResultSource) : AppScreen()
}

enum class ResultSource { LiveCamera, PhotoSwap }

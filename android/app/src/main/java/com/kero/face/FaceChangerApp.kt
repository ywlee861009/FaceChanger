package com.kero.face

import android.app.Application
import com.kero.face.di.AppContainer

class FaceChangerApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

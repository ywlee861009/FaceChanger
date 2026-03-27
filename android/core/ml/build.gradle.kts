plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.kero.face.core.ml"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    androidResources {
        noCompress += "tflite"
    }
}

dependencies {
    implementation(libs.mediapipe.vision)
    implementation(libs.kotlinx.coroutines.android)

    implementation(project(":core:model"))
}

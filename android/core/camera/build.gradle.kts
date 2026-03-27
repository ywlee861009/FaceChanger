plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.kero.face.core.camera"
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
}

dependencies {
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.compose)
    implementation(libs.camerax.effects)
    implementation(libs.kotlinx.coroutines.android)

    implementation(project(":core:model"))
}

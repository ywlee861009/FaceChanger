# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FaceChanger is an Android app (package: `com.kero.face`) built with Jetpack Compose and Material Design 3. The project is a single-module Gradle setup under the `android/` directory.

## Build & Run Commands

All Gradle commands must be run from the `android/` directory:

```bash
cd android

./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install debug build on connected device

./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests on device/emulator

./gradlew test --tests "com.kero.face.ExampleUnitTest.addition_isCorrect"  # Run single test
```

## 필수 요구사항

1. **코드 수정 후 반드시 빌드 검증:** 코드를 수정한 뒤 답변을 주기 전에 반드시 `./gradlew assembleDebug`를 실행하여 빌드가 성공하는지 확인할 것.
2. **Compose + MVI 아키텍처:** UI는 Jetpack Compose, 상태 관리는 MVI(Model-View-Intent) 패턴을 따를 것. 각 화면은 Intent(사용자 액션) → Reducer/ViewModel → State → Composable UI 흐름을 유지한다.
3. **멀티모듈 프로젝트:** 기능별/레이어별로 Gradle 모듈을 분리하여 진행할 것. (예: `:app`, `:core:ui`, `:core:data`, `:feature:*` 등)
4. **모듈화 + 캡슐화 + 객체지향 원칙:** 모듈 간 의존성을 최소화하고, 내부 구현은 `internal`로 캡슐화하며, SOLID 원칙을 준수할 것.

## Architecture

- **UI Framework:** Jetpack Compose with Material 3, Compose BOM 2024.09.00
- **Architecture Pattern:** MVI (Model-View-Intent)
- **Project Structure:** Multi-module (feature-based + layer-based)
- **Entry Point:** `MainActivity.kt` — ComponentActivity using `enableEdgeToEdge()` and Scaffold
- **Theming:** Custom Material 3 theme (`ui/theme/`) with dynamic color support on Android 12+
- **Build Config:** AGP 9.1.0, Kotlin 2.2.10, Java 11, minSdk 33, targetSdk 36
- **Dependency Management:** Gradle version catalog at `android/gradle/libs.versions.toml`

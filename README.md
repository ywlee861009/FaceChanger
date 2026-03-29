# FaceChanger

사람과 강아지의 얼굴을 교환하는 Android 앱입니다.
실시간 카메라 모드와 갤러리 사진 선택 모드를 모두 지원합니다.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| **실시간 얼굴 교환** | 전면 카메라로 사람과 강아지를 함께 비추면 실시간으로 얼굴 교환 |
| **사진 얼굴 교환** | 갤러리에서 사진을 선택해 얼굴 교환 처리 |
| **결과 저장/공유** | 교환된 이미지를 갤러리 저장 또는 외부 앱으로 공유 |
| **커스텀 갤러리 피커** | MediaStore 기반 앨범 분류 + 3열 그리드 갤러리 |

---

## 앱 플로우

```
Splash (1.5초)
  └→ Home
       ├→ [실시간 변경] → 카메라 권한
       │                    └→ LiveCamera (사람 + 강아지 동시 인식)
       │                         └→ 촬영 → Result (저장 / 공유 / 다시 찍기)
       └→ [사진 변경] → 미디어 권한
                          └→ Gallery (앨범 선택 + 사진 그리드)
                               └→ PhotoSwap (ML 분석)
                                    └→ Result (저장 / 공유 / 다시 하기)
```

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| UI | Jetpack Compose, Material Design 3 |
| 아키텍처 | MVI (Model-View-Intent), 멀티모듈 |
| ML | MediaPipe Face Landmarker, EfficientDet Lite0 (강아지 감지) |
| 카메라 | CameraX 1.5.0-alpha06 |
| 이미지 로딩 | Coil 3.1.0 |
| 비동기 | Kotlin Coroutines 1.10.1 |
| 빌드 | AGP 9.1.0, Kotlin 2.2.10 |
| 최소 SDK | API 33 (Android 13) |

---

## 프로젝트 구조

```
android/
├── app/                    # MainActivity, AppScreen, AppContainer (진입점)
├── core/
│   ├── model/              # DetectionResult, BoundingBox, FaceLandmarks, SwapRegion
│   ├── ml/                 # DetectionEngine (Face Landmarker + Dog Detector)
│   ├── camera/             # CameraManager, FrameAnalyzer
│   └── ui/                 # FcTheme, 공유 컴포넌트 (FcButton, FcCard 등)
└── feature/
    ├── splash/             # 스플래시 애니메이션
    ├── home/               # 홈 화면 (모드 선택)
    ├── faceswap/           # 실시간 카메라 + 얼굴 교환
    ├── gallery/            # 커스텀 갤러리 피커
    ├── photoswap/          # 사진 ML 분석
    └── result/             # 결과 화면 (저장/공유)
```

### 모듈 의존성

```
:app
  ├── :feature:splash, :feature:home, :feature:faceswap
  ├── :feature:gallery, :feature:photoswap, :feature:result
  ├── :core:camera, :core:ml, :core:ui
  └── :core:model

:feature:faceswap  →  :core:camera, :core:ml, :core:ui, :core:model
:feature:gallery   →  :core:ui
:feature:photoswap →  :core:ml, :core:ui, :core:model
:core:ml           →  :core:model
:core:camera       →  (독립)
:core:ui           →  (독립)
```

---

## 네비게이션

별도 Navigation 라이브러리 없이 `MainActivity`에서 리스트 기반 back stack으로 구현합니다.

```kotlin
// AppScreen sealed class
sealed class AppScreen {
    object Splash : AppScreen()
    object Home : AppScreen()
    object LiveCamera : AppScreen()
    object Gallery : AppScreen()
    object PhotoSwap : AppScreen()
    data class Result(val source: ResultSource) : AppScreen()
}
```

- `push` / `pop` / `replaceTop` 헬퍼로 화면 전환
- Home에서 뒤로가기 두 번 → 앱 종료 (2초 이내)

---

## ML 파이프라인

### 실시간 모드 (`DetectionEngine.processFrame`)
```
CameraX ImageAnalysis → ByteBuffer → Bitmap (회전 보정)
  └→ FaceLandmarkerProcessor  →  FaceLandmarks (468개 랜드마크)
  └→ DogDetectorProcessor     →  BoundingBox (EfficientDet → 얼굴 영역 추정)
  └→ DetectionResult → FaceSwapReducer → UI 상태 업데이트
```

### 정지 이미지 모드 (`DetectionEngine.detectBitmap`)
```
Gallery URI → Bitmap (EXIF 회전 보정, ARGB_8888 변환)
  └→ FaceLandmarker (IMAGE 모드) → FaceLandmarks
  └→ ObjectDetector (IMAGE 모드) → BoundingBox
  └→ DetectionResult → PhotoSwap UI
```

### 강아지 얼굴 영역 추정
전신 bbox의 종횡비로 자세를 추정해 얼굴 영역을 계산합니다.

| 종횡비 | 추정 자세 | 얼굴 영역 |
|--------|-----------|-----------|
| > 1.3 | 누운 자세 | 상단 50%, 가로 55% |
| 0.8 ~ 1.3 | 정면 클로즈업 | 상단 70%, 가로 75% |
| < 0.8 | 서있음/앉음 | 상단 28%, 가로 60% |

---

## MVI 패턴

모든 Feature 모듈은 동일한 MVI 구조를 따릅니다.

```
사용자 액션
  └→ ViewModel.dispatch(Intent)
       └→ state.update { ... }  +  effect.send(Effect)
            └→ Composable이 collectAsStateWithLifecycle() 로 렌더링
                  └→ LaunchedEffect { effect.receiveAsFlow().collect { ... } }
```

---

## 빌드 및 실행

> 모든 Gradle 명령은 `android/` 디렉터리에서 실행합니다.

```bash
cd android

# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# 연결된 기기에 설치
./gradlew installDebug

# 단위 테스트
./gradlew test

# 기기/에뮬레이터 테스트
./gradlew connectedAndroidTest
```

---

## 권한

| 권한 | 용도 | 필요 시점 |
|------|------|-----------|
| `CAMERA` | 실시간 카메라 프리뷰 및 촬영 | 실시간 변경 진입 시 |
| `READ_MEDIA_IMAGES` | 갤러리 사진 열람 (API 33+) | 사진 변경 진입 시 |

권한 미허용 시 각 화면에서 안내 UI를 표시하고 설정으로 이동하는 버튼을 제공합니다.

---

## 디자인 시스템

파스텔톤 기반의 따뜻하고 아기자기한 스타일을 사용합니다.

### 주요 색상

| 이름 | 용도 | Hex |
|------|------|-----|
| PeachPink | Primary, 주요 액션 | `#FF8FAB` |
| SoftCoral | Primary Container | `#FFB3C1` |
| WarmCream | Surface Variant | `#FFF5E4` |
| SkyLavender | Secondary | `#C8B6FF` |
| MintGreen | Tertiary, 성공 상태 | `#B8F3CE` |
| DarkCocoa | 주요 텍스트 | `#3D2C2E` |

### 스페이싱 토큰

| 토큰 | 값 |
|------|----|
| xs | 4dp |
| sm | 8dp |
| md | 16dp |
| lg | 24dp |
| xl | 32dp |
| xxl | 48dp |

테마 접근: `FcTheme.colors`, `FcTheme.spacing`, `FcTheme.shapes`

---

## 구현 현황

- [x] 스플래시 애니메이션 (페이즈 1: 아이콘 교차 → 페이즈 2: 타이틀 등장)
- [x] 홈 화면 (실시간/사진 모드 선택)
- [x] 실시간 카메라 + MediaPipe 얼굴/강아지 감지
- [x] 커스텀 갤러리 피커 (MediaStore, 앨범 분류)
- [x] 사진 ML 분석 (EXIF 회전 보정 포함)
- [x] 결과 화면 (UI 레이아웃)
- [ ] 실제 얼굴 교환 합성 처리
- [ ] 결과 이미지 갤러리 저장
- [ ] 결과 이미지 공유

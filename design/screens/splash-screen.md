# 스플래시 화면 스펙

## 개요

앱 실행 시 표시되는 첫 화면. 총 1.5초 동안 표시되며, 이 시간 동안 앱 초기화 작업을 수행한다.

---

## 레이아웃

```
┌─────────────────────────────┐
│                             │
│                             │
│                             │
│        ╭───╮  ╭───╮        │  ← 동그란 얼굴 아이콘 2개 (화면 중앙)
│        │ 🐶│  │ 👤│        │
│        ╰───╯  ╰───╯        │
│                             │
│         FaceChanger         │  ← 앱 제목 (페이즈 2에서 등장)
│                             │
│           ●●●               │  ← 로딩 인디케이터 (중앙 살짝 하단)
│                             │
└─────────────────────────────┘
```

---

## 애니메이션 타임라인

### Phase 1 — 얼굴 교환 애니메이션 (0ms ~ 1000ms)

- 화면 중앙에 동그란 얼굴 아이콘 2개 (사람 / 강아지) 가 나란히 배치
- 두 얼굴이 서로의 위치로 **부드럽게 이동** (swap)
  - 이동 커브: `EaseInOut`
  - 이동 궤적: 아치형 (위쪽으로 살짝 휘어지며 교차)
- 1000ms 시점에 교환 완료

```
0ms                        1000ms
[🐶]──────────────────────▶[👤]
      ╲                  ╱
       ╲                ╱
        ╲              ╱
[👤]──────────────────────▶[🐶]
```

### Phase 2 — 타이틀 등장 + 얼굴 페이드아웃 (1000ms ~ 1500ms)

- 얼굴 아이콘 2개: **fade-out** (opacity 1.0 → 0.0, 500ms)
- 앱 제목 "FaceChanger":
  - 화면 중앙에서 **scale-up** 등장 (scale 0.5 → 1.0)
  - **fade-in** (opacity 0.0 → 1.0)
  - 커브: `EaseOut`
  - 지속 시간: 500ms

```
1000ms                     1500ms
[🐶] [👤]  ──fade-out──▶  (사라짐)
  (없음)   ──scale-up──▶  FaceChanger
```

### Phase 3 — 완료 (1500ms)

- 홈 화면으로 전환

---

## 로딩 인디케이터

- **위치**: 화면 중앙 기준 하단 (예: 중앙에서 +120dp)
- **스타일**: 동글동글한 점 3개가 좌우로 이어지는 애니메이션 (Bouncing Dots)
- **표시 시점**: 앱 진입 직후부터 초기화 완료 전까지 표시
- **숨김 시점**: Phase 2 시작 시점 (1000ms) 또는 초기화 완료 시점 중 늦은 쪽

---

## 초기화 작업 (백그라운드)

스플래시 화면이 표시되는 동안 아래 작업을 수행한다.

| 작업 | 설명 |
|------|------|
| 앱 설정 로드 | SharedPreferences 또는 DataStore에서 유저 설정 불러오기 |
| ML 모델 초기화 | 얼굴 인식 / 교환 모델 로드 (최초 실행 시 시간 소요 가능) |
| 권한 상태 확인 | 카메라, 미디어 권한 상태 사전 확인 |

> **주의**: 초기화 작업이 1.5초를 초과할 경우, 완료될 때까지 스플래시를 유지한다.
> 단, 로딩 인디케이터는 계속 표시하여 사용자에게 진행 중임을 알린다.

---

## 디자인 명세

| 항목 | 값 |
|------|----|
| 배경색 | 앱 Primary 컬러 또는 흰색 (테마에 따라) |
| 얼굴 아이콘 크기 | 72dp × 72dp |
| 얼굴 아이콘 간격 | 24dp |
| 아이콘 모양 | 원형 (clip to circle) |
| 앱 제목 폰트 크기 | 32sp |
| 앱 제목 폰트 웨이트 | Bold |
| 로딩 인디케이터 크기 | 점 직경 8dp, 간격 6dp |
| 로딩 인디케이터 위치 | 화면 중앙 Y + 120dp |

---

## MVI 상태 정의 (참고)

```kotlin
// State
data class SplashState(
    val phase: SplashPhase = SplashPhase.FACE_SWAP,
    val isInitialized: Boolean = false
)

enum class SplashPhase {
    FACE_SWAP,    // Phase 1: 얼굴 교환 애니메이션
    TITLE_IN,     // Phase 2: 타이틀 등장 + 얼굴 페이드아웃
    DONE          // Phase 3: 완료 → 홈 화면으로 전환
}

// Intent
sealed class SplashIntent {
    object AnimationPhase1Done : SplashIntent()
    object AnimationPhase2Done : SplashIntent()
    object InitializationDone : SplashIntent()
}
```

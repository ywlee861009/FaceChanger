# FaceChanger 디자인 요구사항

강아지와 사람의 얼굴을 바꿔주는 카메라 기반 Android 앱

## 문서 목록

| 문서 | 설명 |
|------|------|
| [스타일 가이드](style-guide.md) | 색상, 타이포그래피, 아이콘 등 비주얼 가이드라인 |
| [사용자 플로우](user-flow.md) | 주요 사용 시나리오 및 화면 전환 흐름 |

## 화면별 설계 (screens/)

각 파일은 스타일 요약을 포함하여 독립적으로 디자인 가능합니다.

| 파일 | 화면 |
|------|------|
| [01-splash.md](screens/01-splash.md) | 스플래시 |
| [02-camera-main.md](screens/02-camera-main.md) | 메인 카메라 (핵심 화면) |
| [03-processing.md](screens/03-processing.md) | 얼굴 교환 처리 중 |
| [04-result.md](screens/04-result.md) | 결과 확인 |
| [05-gallery.md](screens/05-gallery.md) | 내 컬렉션 (갤러리) |
| [06-settings.md](screens/06-settings.md) | 설정 |

## 핵심 콘셉트

- **타겟 사용자:** 반려견과 함께하는 일반 사용자 (전 연령대)
- **앱 톤앤매너:** 아기자기하고 귀여운 감성, 밝고 따뜻한 분위기
- **핵심 기능:** 카메라로 사람과 강아지 얼굴을 인식하여 서로 바꿔주기
- **플랫폼:** Android (minSdk 33), Jetpack Compose + Material 3

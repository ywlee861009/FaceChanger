# 스타일 가이드

## 1. 디자인 원칙

- **아기자기함:** 둥근 모서리, 부드러운 그림자, 귀여운 일러스트 요소 활용
- **직관적:** 카메라 앱이므로 최소한의 UI로 핵심 기능에 집중
- **밝고 따뜻함:** 파스텔톤 기반의 따뜻한 색상 팔레트

## 2. 색상 팔레트

### Primary Colors
| 이름 | 용도 | Hex |
|------|------|-----|
| Peach Pink | 주요 액션 버튼, 강조 | `#FF8FAB` |
| Soft Coral | 보조 강조, 활성 상태 | `#FFB3C1` |
| Warm Cream | 배경, 카드 | `#FFF5E4` |

### Secondary Colors
| 이름 | 용도 | Hex |
|------|------|-----|
| Sky Lavender | 보조 버튼, 태그 | `#C8B6FF` |
| Mint Green | 성공 상태, 완료 표시 | `#B8F3CE` |
| Puppy Brown | 아이콘, 텍스트 강조 | `#8B6F47` |

### Neutral Colors
| 이름 | 용도 | Hex |
|------|------|-----|
| Dark Cocoa | 주요 텍스트 | `#3D2C2E` |
| Warm Gray | 보조 텍스트 | `#9E8E8E` |
| Light Cream | 배경 | `#FFFAF3` |
| Pure White | 카드, 오버레이 | `#FFFFFF` |

## 3. 타이포그래피

- **폰트:** Pretendard (한글/영문 통합)
- **제목 (H1):** 24sp, Bold, Dark Cocoa
- **소제목 (H2):** 18sp, SemiBold, Dark Cocoa
- **본문:** 14sp, Regular, Dark Cocoa
- **캡션:** 12sp, Regular, Warm Gray
- **버튼 텍스트:** 16sp, SemiBold, White

## 4. 모서리 및 형태

- **버튼:** Corner Radius 24dp (완전 둥근 느낌)
- **카드:** Corner Radius 16dp
- **바텀시트:** Corner Radius 24dp (상단)
- **아이콘 버튼:** 원형 (50% radius)
- **이미지 썸네일:** Corner Radius 12dp

## 5. 그림자 및 Elevation

- **카드:** Elevation 2dp, 부드러운 그림자
- **FAB (촬영 버튼):** Elevation 6dp
- **바텀시트:** Elevation 8dp
- **일반 버튼:** Elevation 1dp

## 6. 아이콘 스타일

- **스타일:** Rounded, Filled 계열 (Material Icons Rounded)
- **커스텀 아이콘:** 강아지 발바닥, 하트, 별 등 귀여운 장식 요소
- **사이즈:** 24dp (기본), 32dp (강조), 48dp (메인 액션)

## 7. 애니메이션

- **화면 전환:** Fade + SlideUp (300ms, EaseInOut)
- **버튼 터치:** Scale 0.95 → 1.0 (150ms)
- **얼굴 교환 완료:** 반짝이는 파티클 효과 + 귀여운 사운드
- **로딩:** 강아지 발바닥이 톡톡 찍히는 애니메이션
- **셔터:** 하트 모양 셔터 애니메이션

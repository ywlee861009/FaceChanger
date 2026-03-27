# 02. 메인 카메라 화면

## 앱 개요
FaceChanger — 강아지와 사람의 얼굴을 바꿔주는 카메라 앱. 아기자기하고 귀여운 감성.

## 스타일 요약
- **Primary:** Peach Pink `#FF8FAB`, Soft Coral `#FFB3C1`, Warm Cream `#FFF5E4`
- **Secondary:** Sky Lavender `#C8B6FF`, Mint Green `#B8F3CE`, Puppy Brown `#8B6F47`
- **텍스트:** Dark Cocoa `#3D2C2E`, 보조 Warm Gray `#9E8E8E`
- **배경:** Light Cream `#FFFAF3`, Pure White `#FFFFFF`
- **폰트:** Pretendard / 제목 24sp Bold, 소제목 18sp SemiBold, 본문 14sp Regular, 캡션 12sp, 버튼 16sp SemiBold
- **모서리:** 버튼 24dp, 카드 16dp, 아이콘 버튼 원형, 썸네일 12dp
- **톤앤매너:** 파스텔톤, 둥근 형태, 부드러운 그림자

## 화면 구성

앱의 핵심 화면. 카메라 프리뷰가 전체 화면을 차지한다.

### 상단 바 (카메라 위 오버레이)
- **좌측:** 설정 아이콘 버튼 (원형, 반투명 White 배경 `#FFFFFF80`, 아이콘 Dark Cocoa)
- **우측:** 카메라 전환 버튼 (전면/후면 전환, 동일 스타일)

### 중앙 — 카메라 프리뷰
- 전체 화면 카메라 프리뷰 (배경 전체)
- **얼굴 인식 시:**
  - 인식된 얼굴에 둥근 점선 테두리 (Peach Pink, 2dp 두께)
  - 테두리 위에 라벨 태그: "사람" 또는 "강아지" (Soft Coral 배경, White 텍스트, 12sp, radius 8dp)
- **얼굴 미인식 시:**
  - 화면 중앙에 안내 카드 (Pure White, radius 16dp, elevation 2dp)
  - 카드 내부: 강아지와 사람이 나란히 서있는 작은 일러스트
  - 텍스트: "사람과 강아지를 함께 비춰주세요!" (14sp, Dark Cocoa)

### 하단 컨트롤 바
- 반투명 배경 (`#FFFFFF99`), 상단 radius 24dp
- **좌측:** 최근 결과 썸네일 (48x48dp, radius 12dp, 테두리 2dp White)
  - 결과 없을 시: 빈 원형 + 갤러리 아이콘
- **중앙:** 촬영 버튼 (72dp 원형)
  - 활성 상태 (사람+강아지 인식): Peach Pink 배경, 강아지 발바닥 아이콘 (White, 32dp)
  - 비활성 상태: Warm Gray 배경, 터치 불가
  - 외곽에 Soft Coral 링 (4dp)
- **우측:** 갤러리에서 사진 선택 버튼 (48dp 원형, 반투명 White 배경, 사진 아이콘)

## 전환
- 설정 버튼 → **06. 설정 화면** (SlideUp)
- 촬영 버튼 → **03. 처리 화면** (셔터 애니메이션 후 Fade)
- 썸네일 탭 → **05. 갤러리 화면** (SlideUp)
- 갤러리 선택 → 기기 사진 선택기 → **03. 처리 화면**

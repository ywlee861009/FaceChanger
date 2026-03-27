# 04. 결과 화면

## 앱 개요
FaceChanger — 강아지와 사람의 얼굴을 바꿔주는 카메라 앱. 아기자기하고 귀여운 감성.

## 스타일 요약
- **Primary:** Peach Pink `#FF8FAB`, Soft Coral `#FFB3C1`, Warm Cream `#FFF5E4`
- **Secondary:** Sky Lavender `#C8B6FF`, Mint Green `#B8F3CE`, Puppy Brown `#8B6F47`
- **텍스트:** Dark Cocoa `#3D2C2E`, 보조 Warm Gray `#9E8E8E`
- **배경:** Light Cream `#FFFAF3`, Pure White `#FFFFFF`
- **폰트:** Pretendard / 제목 24sp Bold, 소제목 18sp SemiBold, 본문 14sp Regular, 버튼 16sp SemiBold
- **모서리:** 버튼 24dp, 카드 16dp, 아이콘 버튼 원형
- **톤앤매너:** 파스텔톤, 둥근 형태, 부드러운 그림자

## 화면 구성

얼굴 교환이 완료된 결과를 보여주는 화면.

### 상단 바
- 배경: Light Cream
- **좌측:** 닫기 버튼 (X 아이콘, 원형, 반투명 배경)
- **중앙:** "완성!" 텍스트 (18sp SemiBold, Dark Cocoa) + 작은 하트 아이콘 (Peach Pink, 16dp)

### 중앙 — 결과 이미지
- 큰 카드 형태 (Pure White 배경, radius 16dp, elevation 2dp)
- 카드 내부: 결과 이미지 (radius 12dp, 좌우 패딩 16dp)
- 이미지 위에 **비교 슬라이더:**
  - 수직 구분선 (White, 2dp 두께)
  - 구분선 중앙에 둥근 핸들 (White 원형, 32dp, elevation 4dp, 좌우 화살표 아이콘)
  - 좌측: 원본 이미지 / 우측: 결과 이미지
  - 드래그하여 비교 가능

### 하단 — 액션 버튼 영역
- 배경: Pure White, 상단 radius 24dp, elevation 4dp
- 3개 버튼 가로 균등 배치:

| 버튼 | 아이콘 | 배경색 | 텍스트 |
|------|--------|--------|--------|
| 저장 | 다운로드 아이콘 | Mint Green | "저장" (Dark Cocoa) |
| 공유 | 공유 아이콘 | Sky Lavender | "공유" (Dark Cocoa) |
| 다시 찍기 | 카메라 아이콘 | Soft Coral | "다시 찍기" (Dark Cocoa) |

- 버튼 스타일: 세로 배치 (아이콘 위 + 텍스트 아래), 각 56dp 원형 아이콘 배경 + 아래 캡션
- 아이콘: White, 24dp
- 캡션: 12sp Regular, Dark Cocoa

### 저장 완료 피드백
- 하단에서 올라오는 토스트 메시지
- "갤러리에 저장했어요!" + 체크 아이콘 (Mint Green)
- Warm Cream 배경, radius 24dp, 2초 후 자동 사라짐

## 전환
- 닫기(X) → **02. 메인 카메라 화면** (SlideDown)
- 다시 찍기 → **02. 메인 카메라 화면** (SlideDown)
- 공유 → Android 시스템 공유 시트

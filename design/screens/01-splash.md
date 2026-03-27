# 01. 스플래시 화면

## 앱 개요
FaceChanger — 강아지와 사람의 얼굴을 바꿔주는 카메라 앱. 아기자기하고 귀여운 감성.

## 스타일 요약
- **Primary:** Peach Pink `#FF8FAB`, Soft Coral `#FFB3C1`, Warm Cream `#FFF5E4`
- **Secondary:** Sky Lavender `#C8B6FF`, Mint Green `#B8F3CE`, Puppy Brown `#8B6F47`
- **텍스트:** Dark Cocoa `#3D2C2E`, 보조 Warm Gray `#9E8E8E`
- **배경:** Light Cream `#FFFAF3`
- **폰트:** Pretendard / 제목 24sp Bold, 본문 14sp Regular
- **모서리:** 버튼 24dp, 카드 16dp, 아이콘 버튼 원형
- **톤앤매너:** 파스텔톤, 둥근 형태, 부드러운 그림자

## 화면 구성

### 배경
- Light Cream → Warm Cream 그라데이션 (상단→하단)
- 떠다니는 강아지 발바닥 패턴 (반투명, 장식용)

### 중앙 요소
- **앱 로고:** 강아지와 사람 얼굴이 합쳐진 아이콘 (크게, 화면 중앙)
- **앱 이름:** "FaceChanger" 텍스트 (24sp Bold, Dark Cocoa)
- **서브 텍스트:** "강아지와 얼굴을 바꿔보세요!" (14sp, Warm Gray)

### 동작
- 표시 시간: 1.5초 후 메인 카메라 화면으로 자동 전환
- 로고 등장 시 부드러운 Scale Up 애니메이션 (0.8 → 1.0, 500ms)

## 전환
- 다음 화면: **02. 메인 카메라 화면**
- 전환 효과: Fade Out (300ms)

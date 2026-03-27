# 05. 갤러리 화면 (내 컬렉션)

## 앱 개요
FaceChanger — 강아지와 사람의 얼굴을 바꿔주는 카메라 앱. 아기자기하고 귀여운 감성.

## 스타일 요약
- **Primary:** Peach Pink `#FF8FAB`, Soft Coral `#FFB3C1`, Warm Cream `#FFF5E4`
- **Secondary:** Sky Lavender `#C8B6FF`, Mint Green `#B8F3CE`, Puppy Brown `#8B6F47`
- **텍스트:** Dark Cocoa `#3D2C2E`, 보조 Warm Gray `#9E8E8E`
- **배경:** Light Cream `#FFFAF3`, Pure White `#FFFFFF`
- **폰트:** Pretendard / 제목 24sp Bold, 소제목 18sp SemiBold, 본문 14sp Regular, 캡션 12sp
- **모서리:** 버튼 24dp, 카드 16dp, 썸네일 12dp
- **톤앤매너:** 파스텔톤, 둥근 형태, 부드러운 그림자

## 화면 구성

저장된 결과 이미지들을 모아보는 화면.

### 상단 바
- 배경: Light Cream
- **좌측:** 뒤로가기 화살표 (Dark Cocoa)
- **중앙:** "내 컬렉션" (18sp SemiBold, Dark Cocoa) + 강아지 발바닥 아이콘 (Puppy Brown, 20dp)

### 컨텐츠 — 이미지 그리드
- 2열 그리드 레이아웃
- 좌우 패딩 16dp, 항목 간격 12dp
- 각 항목:
  - 썸네일 이미지 (radius 12dp, 1:1 비율)
  - 썸네일 하단에 날짜 텍스트 (12sp, Warm Gray, 예: "3월 27일")
  - elevation 1dp, Pure White 카드 배경, radius 12dp
- **탭:** 해당 결과 화면으로 이동
- **롱프레스:** 삭제 확인 다이얼로그
  - "이 사진을 삭제할까요?" (14sp, Dark Cocoa)
  - 버튼: "삭제" (Peach Pink) / "취소" (Warm Gray)

### 빈 상태 (결과가 없을 때)
- 화면 중앙 수직 정렬
- 귀여운 강아지 일러스트 (120dp)
- "아직 바꾼 얼굴이 없어요!" (18sp SemiBold, Dark Cocoa)
- "첫 번째 얼굴을 바꿔보세요" (14sp, Warm Gray)
- 카메라 바로가기 버튼 (Peach Pink 배경, radius 24dp, "촬영하러 가기" 16sp White)

## 전환
- 뒤로가기 → **02. 메인 카메라 화면** (SlideDown)
- 항목 탭 → **04. 결과 화면** (Fade)
- 촬영하러 가기 → **02. 메인 카메라 화면** (SlideDown)

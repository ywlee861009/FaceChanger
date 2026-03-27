# 03. 얼굴 교환 처리 화면

## 앱 개요
FaceChanger — 강아지와 사람의 얼굴을 바꿔주는 카메라 앱. 아기자기하고 귀여운 감성.

## 스타일 요약
- **Primary:** Peach Pink `#FF8FAB`, Soft Coral `#FFB3C1`, Warm Cream `#FFF5E4`
- **Secondary:** Sky Lavender `#C8B6FF`, Mint Green `#B8F3CE`, Puppy Brown `#8B6F47`
- **텍스트:** Dark Cocoa `#3D2C2E`, 보조 Warm Gray `#9E8E8E`
- **배경:** Light Cream `#FFFAF3`
- **폰트:** Pretendard / 소제목 18sp SemiBold, 본문 14sp Regular
- **모서리:** 카드 16dp
- **톤앤매너:** 파스텔톤, 둥근 형태, 부드러운 그림자

## 화면 구성

촬영 후 AI가 얼굴 교환을 처리하는 중간 화면.

### 배경
- 촬영한 원본 이미지를 블러 처리 (blur radius 20dp)
- 블러 위에 반투명 Light Cream 오버레이 (`#FFFAF3CC`)

### 중앙 컨텐츠 (수직 중앙 정렬)
- **로딩 애니메이션:** 강아지 발바닥 3개가 순서대로 톡톡 나타나는 애니메이션
  - 발바닥 색상: Peach Pink, Soft Coral, Sky Lavender (순서대로)
  - 크기: 각 32dp
  - 애니메이션: 하나씩 Scale Up(0→1) + Fade In, 0.3초 간격, 무한 반복
- **텍스트:** "얼굴을 바꾸는 중..." (18sp SemiBold, Dark Cocoa)
  - 로딩 애니메이션 아래 16dp 간격
- **보조 텍스트:** "잠깐만 기다려주세요!" (14sp, Warm Gray)

### 처리 완료 시
- 발바닥 애니메이션 → 큰 하트로 변환 (Peach Pink)
- 반짝이 파티클 효과 (Gold `#FFD700`, 주변에 흩뿌려짐)
- 0.5초 후 결과 화면으로 전환

## 에러 상태
- 처리 실패 시: 슬픈 표정의 강아지 일러스트
- 텍스트: "앗, 문제가 생겼어요!" (18sp SemiBold, Dark Cocoa)
- 버튼: "다시 시도" (Peach Pink, radius 24dp, 16sp SemiBold White)

## 전환
- 처리 완료 → **04. 결과 화면** (Scale Up + Fade, 500ms)
- 에러 → 다시 시도 → **02. 메인 카메라 화면**

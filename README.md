<p align="middle" style="margin: 0; padding: 0;">
  <img width="360px" src="https://github.com/user-attachments/assets/dc348de4-aecb-4ce0-816e-08062ab7ed74">
</p>

<p align="middle">
<b>[한화시스템 BEYOND SW캠프 12기] Final Project</b>
<br><b>"GrapeField" 공연/전시 정보 통합 제공 및 커뮤니티 플랫폼</b>
<br>🧑🏻‍🌾 CATCHER 파수꾼 팀 🍇

## 😃 팀원 소개
<figure>
    <table>
      <tr>
        <td align="center"><img src="https://github.com/user-attachments/assets/fc09670f-0100-4deb-a070-a975200e5b44" width="180px"/></td>
        <td align="center"><img src="https://github.com/user-attachments/assets/e1378ccf-4afa-48cd-877d-e64b2ac69c19" width="180px"/></td>
        <td align="center"><img src="https://github.com/user-attachments/assets/e2dc5942-a318-4082-98e9-79a3919b0a07" width="180px"/></td>
	    <td align="center"><img src="https://github.com/user-attachments/assets/05128582-7e08-4fb5-a911-cf095af55af3" width="180px"/></td>
      </tr>
      <tr>
        <td align="center">팀장: <a href="https://github.com/bdt6246">김혜정</a></td>
        <td align="center">팀원: <a href="https://github.com/daydeiday">곽효림</a></td>
        <td align="center">팀원: <a href="https://github.com/J0a0J">김지원</a></td>
        <td align="center">팀원: <a href="https://github.com/s00ya">정지수</a></a></td>
      </tr>
    </table>
</figure>

---

### 목차
- [🛠 기술 스택](#-기술-스택)
- [📚 OOO 데모 사이트 링크](#-enadu-사이트-바로가기)
- [🎁 OOO 서비스 소개](#-enadu-서비스-소개)
- [📈 OOO 프로젝트 설계](#-프로젝트-설계)
- [📂 프로젝트 폴더 바로가기](#-프로젝트-폴더-바로가기)
<br><br>


## 📝 프로젝트 소개

**GrapeField**는 공연(뮤지컬, 콘서트, 연극), 전시회(전시, 박람회) 등  
**오프라인 문화 콘텐츠**에 대한 정보 제공과 **실시간 소통**이 가능한 통합 플랫폼입니다.

단순한 정보 제공이나 일정 안내를 넘어,  
이용자들이 오프라인 문화 행사에 대해 실시간으로 **소통하고 후기를 공유할 수 있는 커뮤니티 기능**을 강화했습니다.

---

## 🎯 개발 배경

코로나 팬데믹 이후, 오프라인 문화 행사에 대한 수요가 크게 증가했지만  
다음과 같은 문제점들이 있었습니다:

### ❌ 부족한 소통 공간
- 관람 후기를 **체계적으로 기록하거나**,  
  **다른 관람객들과 의견을 나눌 수 있는 구조화된 공간**이 부족했습니다.

### ❌ 실시간 정보 확인의 어려움
- 관람 직전, **주차 상황 / 대기 시간 / 혼잡도** 등  
  **현장 정보**를 실시간으로 확인하기 어려웠습니다.

### ❌ 채팅 접근성 문제
- 실시간 채팅은 **원하는 구간을 다시 찾기 어렵고**,  
  일정 기간 후 **삭제되어 기록이 남지 않는** 문제가 있었습니다.


이러한 문제를 해결하기 위해 **GrapeField** 서비스를 기획하게 되었습니다.  
문화 콘텐츠를 즐기는 사람들을 위한 **정보 + 커뮤니티 + 실시간 경험 공유 플랫폼**입니다.

---


## 🔗 화면설계서
- [Figma 화면 설계서](https://www.figma.com/design/a0ICwRU8Sc7fTzA3aDfpTi/GrapeField?node-id=84-5&p=f&t=nB2EEGHnmULDk6D9-0)
---

## 🔗 배포 링크

- [🔗 Grapefield ](https://grapefield.kro.kr/)

<br>

---

## 🛠️ 기술 스택

### 🖥️ Frontend
![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vue.js&logoColor=4FC08D)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge)
![Pinia](https://img.shields.io/badge/Pinia-ffe066?style=for-the-badge&logo=pinia&logoColor=black)
![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge)
![STOMP.js](https://img.shields.io/badge/STOMP.js-6A1B9A?style=for-the-badge)
![SockJS](https://img.shields.io/badge/SockJS-FD4F00?style=for-the-badge)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)


---

## :receipt: 페이지 설명

### 1. 🏠 메인 페이지
- 현재 진행 중이거나 예정된 **공연/전시회 정보를 최신순, 인기순으로 표시**
- **카테고리별 필터링 기능** 제공 (공연, 전시, 박람회 등)
- **추천 행사 및 하이라이트 코너** 제공

### 2. 📅 일정 캘린더
- **공연/전시 일정을 캘린더 형식으로 시각화**
- 예매처 링크, **선예매 / 일반예매** 구분 표시
- **관심 행사 알림 설정 기능**

### 3. 📝 행사 상세 페이지
- 행사 기본 정보 (제목, 일시, 장소, 가격 등) 제공
- **예매 정보 및 링크** 표시
- **후기 게시판 연결** 기능
- 실시간 채팅방 입장 버튼 (개발중)

### 4. 🌟 후기 게시판
- **행사별 후기 게시판 제공**
- **별점, 상세 리뷰, 이미지 첨부** 기능
- **댓글 및 추천 기능**
- **검색 및 필터링 기능**

### 5. 💬 실시간 채팅방
- **행사별 실시간 채팅** 기능
- **과거 채팅 내용 조회 기능**
- - **하이라이트 시간대 자동 탐지 및 북마크 기능** (개발중)
- **주요 정보 핀 고정 기능** (개발중)

### 6. 🙋 마이페이지
- **관심 행사 목록** 확인
- **작성한 후기 관리**
- **알림 설정 및 관리**
- **프로필 설정**

---

## :receipt: 시나리오
## :green_circle: 1. 기본 채팅 입장 테스트

1. [:link: 사이트 접속](https://grapefield.kro.kr)
2. 상단 메뉴에서 로그인 **(`test3@example.com`, `1234` 사용)**
3. 사이드 바에서 채팅 진입
4. [:speech_balloon: 채팅 목록 하단에 전체 채팅방] 버튼 클릭 → 채팅방 리스트 UI 등장
5. 원하는 채팅방 선택 후 입장
6. 메시지 전송 → 전송 완료 확인

#### :repeat: 2. 다중 사용자 실시간 채팅

1. `test01`과 `test02`를 각각 로그인 (다른 브라우저/시크릿 모드 권장)
2. 동일한 공연 상세 페이지에서 채팅방 입장
3. 메시지를 교차로 입력하여 **양방향 실시간 전달** 확인

#### :arrows_counterclockwise: 3. 퇴장 및 재입장 확인

1. 채팅창 상단 메뉴 → [채팅방 나가기] 클릭
2. 다시 동일 페이지에서 채팅방 입장
3. **최근 메시지 이력 조회** 가능 여부 확인

#### :arrows_counterclockwise: 4. 채팅 하이라이트 확인

1. 채팅창에 30초 이내에 20개 이상의 채팅을 전송
2. 20개 넘은 지점에 하이라이트 발생 확인
3. 채팅창 상단에 하이라이트 UI 확인

---

## 📁 디렉토리 구조
### GradpeFiled_Back
아래는 예시용으로만 참고해주기를    
예를 들어 UserBlock의 경우 아래처럼이 아닌
<details>
<summary><strong>📦 src/</strong> 클릭하여 패키지 구조 보기</summary>
<pre>
📁 src  
├── 📁 user  
│   ├── 📁 model  
│   │   ├── 📁 entity  
│   │   │   ├── User.java  
│   │   │   ├── UserBlock.java  
│   │   │   └── UserEventsInterest.java  
│   │   ├── 📁 request  
│   │   │   └── UserCreateReq.java  
│   │   └── 📁 response  
│   │       ├── UserDetailResp.java  
│   │       ├── UserListItemResp.java  
│   │       ├── UserProfileResp.java  
│   │       └── UserBlockResp.java  
│   ├── UserController.java  
│   ├── UserService.java  
│   └── UserRepository.java  
│  
├── 📁 events  
│   ├── 📁 model  
│   │   ├── 📁 entity  
│   │   │   ├── Events.java  
│   │   │   ├── EventsImg.java  
│   │   │   ├── TicketInfo.java  
│   │   │   ├── SeatPrice.java  
│   │   │   ├── Participant.java  
│   │   │   └── Review.java  
│   │   ├── 📁 request  
│   │   │   └── EventCreateReq.java  
│   │   └── 📁 response  
│   │       ├── EventsListItemResp.java  
│   │       ├── EventsDetailResp.java  
│   │       └── TicketInfoResp.java  
│   ├── EventsController.java  
│   ├── EventsService.java  
│   └── EventsRepository.java  
│  
│   ├── 📁 post  
│   │   ├── 📁 model  
│   │   │   ├── 📁 entity  
│   │   │   │   ├── Post.java  
│   │   │   │   ├── PostComment.java  
│   │   │   │   ├── PostAttachment.java  
│   │   │   │   └── PostRecommend.java  
│   │   │   ├── 📁 request  
│   │   │   │   ├── PostCreateReq.java  
│   │   │   │   └── PostCommentReq.java  
│   │   │   └── 📁 response  
│   │   │       ├── PostListItemResp.java  
│   │   │       ├── PostDetailResp.java  
│   │   │       ├── PostCommentResp.java  
│   │   │       └── PostAttachmentResp.java  
│   │   ├── PostController.java  
│   │   ├── PostService.java  
│   │   ├── PostRepository.java  
│   │   ├── PostCommentRepository.java  
│   │   ├── PostAttachmentRepository.java  
│   │   └── PostRecommendRepository.java  │  
├── 📁 config  
│   ├── JwtProperties.java  
│   ├── RedisConfig.java  
│   └── SwaggerConfig.java  
│  
├── 📁 utils  
│   └── JwtUtils.java  
│  
├── 📁 report  
│   └── 📁 model  
│       ├── 📁 entity  
│       │   └── Report.java  
│       └── 📁 request  
│           └── ReportCreateReq.java  

</pre>

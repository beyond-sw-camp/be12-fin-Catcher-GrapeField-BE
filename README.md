# "GrapeField" 공연/전시 정보 통합 제공 및 커뮤니티 플랫폼
<p align="middle" style="margin: 0; padding: 0;">
  <img width="360px" src="https://github.com/user-attachments/assets/dc348de4-aecb-4ce0-816e-08062ab7ed74">
</p>

<p align="middle">
[플레이 데이터] 한화시스템 BEYOND SW캠프 12기
<br>🧑🏻‍🌾 CATCHER 파수꾼 팀 🍇
</p>

## 😃 팀원 소개
<figure>
    <table>
      <tr>
        <td align="center"><img src="" width="180px"/></td>
        <td align="center"><img src="" width="180px"/></td>
        <td align="center"><img src="" width="180px"/></td>
	    <td align="center"><img src="" width="180px"/></td>
      </tr>
      <tr>
        <td align="center">팀장: <a href="https://github.com/bdt6246">김혜정</a></td>
        <td align="center">팀원: <a href="https://github.com/daydeiday">곽효림</a></td>
        <td align="center">팀원: <a href="https://github.com/s00ya" >정지수</a></td>
        <td align="center">: <a href="https://github.com/J0a0J">김지원</a></a></td>
      </tr>
    </table>
</figure>

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

## :receipt: 페이지 설명 및 테스트 시나리오 
1. 회원가입 및 로그인
- 일반 회원가입
- 이메일 인증 통해 비밀번호 찾기
2. 마이페이지
- 사용자 정보: 개인정보(전화번호, 주소 등) 수정 및 회원 탈퇴 기능
- 글 관리: 작성 글, 댓글, 한줄평 모아보기
- 캘린더: 개인 일정 등록, 관리
- 즐겨찾기 관리 **(구현중)**
- 1:1 문의 내역
3. 전시/공연
  - 카테고리별 공연 분류 (뮤지컬, 연극, 콘서트, 전시회, 클래식)
  - 공연 상세 정보 조회 (예매 일정, 공연 일정, 출연진, 공연 상세 정보)
  - 예매처 링크 제공
  - 단일 공연을 위한 게시판, 한줄평 제공
4. 커뮤니티
- 게시글(카테고리, 제목, 내용, 첨부파일)과 함께 작성
- 게시글 조회
- 게시글 상세보기
- 댓글 작성

5. 캘린더
- 일자별 공연/전시 조회
- 날짜 클릭하여 해당 날짜의 공연/전시 리스트 조회
- 카테고리 선택에 따라 해당 카테고리만 조회 기
  

#### :green_circle: 1. 기본 채팅 입장 테스트

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

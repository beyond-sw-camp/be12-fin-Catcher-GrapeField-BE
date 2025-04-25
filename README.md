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

## 📝 프로젝트 소개


> 🍇GrapeField 서비스는 공연/전시와 같은 오프라인 문화행사의 정보를 통합 제공하는 정보의 허브입니다.
 정보 수집부터 실시간 소통, 그리고 그러한 정보들의 아카이빙까지
각 목적에 최적화된 기술 스택을 기반으로 통합 플랫폼을 구축하고자 합니다.


&nbsp;

## 🔧 기술 스택
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![Spring data jpa](https://img.shields.io/badge/Spring%20data%20jpa-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![WebSocket](https://img.shields.io/badge/WebSocket-0084FF?style=for-the-badge&logo=websocket&logoColor=white)
![STOMP](https://img.shields.io/badge/STOMP-6A1B9A?style=for-the-badge&logo=apachekafka&logoColor=white)


&nbsp;
## 시스템 아키텍쳐

&nbsp;

## API 문서

<br>
&nbsp;
## 프론트 주소
- [front 주소](https://grapefield.kro.kr)
- [front git 주소](https://github.com/beyond-sw-camp/be12-fin-catcher-grapefield-FE)


&nbsp;
## 기능테스트




&nbsp;

## 성능테스트
<details>
<summary> review </summary>

### 개선 된 쿼리


-
### 개선 전 성능


### 개선 후 성능


</details>




## GradpeFiled_Back
아래는 예시용으로만 참고해주기를    
예를 들어 UserBlock의 경우 아래처럼이 아닌
<pre>
📁 src  
├── 📁 user  
│   ├── 📁 model  
│   │   ├── 📁 entity  
│   │   │   ├── User.java  
│   │   │   └── UserEventsInterest.java  
│   │   ├── 📁 request  
│   │   │   └── UserCreateReq.java  
│   │   └── 📁 response  
│   │       ├── UserDetailResp.java  
│   │       ├── UserListItemResp.java  
│   │       └── UserProfileResp.java  
│   ├── 📁 userblock   
│   │   └── 📁 model    
│   │       └──  📁 entity  
│   │            └── UserBlock.java  
│   ├── UserController.java  
│   ├── UserService.java  
│   └── UserRepository.java  
</pre>
과 같이 user하위의 userblock이라는 별개의 패키지를 만들어 관리하는 것도 가능하므로    
패키지를 어디까지 쪼갤지는 기능을 구현하면서 담당자가 알아서 정하기

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
│   │   └── PostRecommendRepository.java  
│  
│   └── 📁 chat  
│       ├── 📁 model  
│       │   ├── 📁 entity  
│       │   │   ├── ChatRoom.java  
│       │   │   ├── ChatroomMember.java  
│       │   │   ├── ChatMessageBase.java  
│       │   │   ├── ChatMessageCurrent.java  
│       │   │   ├── ChatMessageArchive.java  
│       │   │   └── ChatHighlight.java  
│       │   ├── 📁 request  
│       │   │   └── ChatSendMessageReq.java  
│       │   └── 📁 response  
│       │       ├── ChatRoomListResp.java  
│       │       ├── ChatRoomDetailResp.java  
│       │       ├── ChatMessageResp.java  
│       │       └── ChatHighlightResp.java  
│       ├── ChatController.java  
│       ├── ChatService.java  
│       ├── ChatRoomRepository.java  
│       ├── ChatMessageRepository.java  
│       └── ChatHighlightRepository.java  
│  
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

GradpeFiled_Back
아래는 예시용으로만 참고해주기를    
예를 들어 UserBlock의 경우 아래처럼이 아닌    
src/user/userblock/model...    
과 같이 user하위의 userblock이라는 별개의 패키지를 만들어 관리하는 것도 가능하므로    
패키지를 어디까지 쪼갤지는 기능을 구현하면서 차차 생각해도 됨     

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

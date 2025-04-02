GradpeFiled_Back
아래는 예시용으로만 참고해주기를
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

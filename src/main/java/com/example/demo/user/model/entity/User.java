package com.example.demo.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String username;
    private String email;
    private String password;
    private String nickname;
    private String profileImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Review> reviewList;

    @OneToMany(mappedBy = "user")
    private List<Post> postList;

    @OneToMany(mappedBy = "user")
    private List<PostComment> commentList;

    @OneToMany(mappedBy = "user")
    private List<PostRecommend> recommendList;

    @OneToMany(mappedBy = "user")
    private List<ChatroomMember> chatroomMemberList;

    @OneToMany(mappedBy = "user")
    private List<ChatMessageCurrent> chatMessageList;

    @OneToMany(mappedBy = "user")
    private List<EventsNotification> notificationList;

    @OneToMany(mappedBy = "user")
    private List<UserEventsInterest> interestList;
}
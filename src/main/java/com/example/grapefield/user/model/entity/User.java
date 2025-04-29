package com.example.grapefield.user.model.entity;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.events.review.model.entity.Review;
import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostComment;
import com.example.grapefield.events.post.model.entity.PostRecommend;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String username;
  @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private String profileImg;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private UserRole role = UserRole.ROLE_USER;
  @Builder.Default
    private int reportCnt = 0;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private AccountStatus status = AccountStatus.UNAUTHENTIC;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

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
    private List<ScheduleNotification> notificationList;

    @OneToMany(mappedBy = "user")
    private List<EventsInterest> interestList;

  @OneToMany(mappedBy = "user")
  private List<EmailVerify> verifyList;

  @OneToMany(mappedBy = "user")
  private List<PersonalSchedule> personalScheduleList;

}
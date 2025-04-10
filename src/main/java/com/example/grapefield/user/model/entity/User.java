package com.example.grapefield.user.model.entity;

import com.example.grapefield.events.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.events.chat.model.entity.ChatroomMember;
import com.example.grapefield.events.model.entity.Review;
import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostComment;
import com.example.grapefield.events.post.model.entity.PostRecommend;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String username;
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
    private List<EventsNotification> notificationList;

    @OneToMany(mappedBy = "user")
    private List<EventsInterest> interestList;

  @OneToMany(mappedBy = "user")
  private List<EmailVerify> verifyList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getRole()));
        return authorities;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
  public boolean isEnabled(){
      return status.equals(AccountStatus.ACTIVE);
    }
}
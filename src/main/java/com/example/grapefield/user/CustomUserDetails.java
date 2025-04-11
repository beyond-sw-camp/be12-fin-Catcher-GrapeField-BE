package com.example.grapefield.user;

import com.example.grapefield.user.model.entity.AccountStatus;
import com.example.grapefield.user.model.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @param user 원본 사용자 객체 접근 메서드 실제 User 엔티티
 */
public record CustomUserDetails(User user) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  @Override
  public String getUsername() {
    return user.getEmail();  // Spring Security 인증용으로는 이메일 사용
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public boolean isEnabled() {
    return user.getStatus().equals(AccountStatus.ACTIVE);
  }

  // User 엔티티의 실제 필드에 접근하는 편의 메서드들
  public String getRealUsername() {
    return user.getUsername();
  }

  public String getEmail() {
    return user.getEmail();
  }
}
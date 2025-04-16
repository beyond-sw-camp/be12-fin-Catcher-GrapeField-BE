package com.example.grapefield.user;

import com.example.grapefield.user.model.entity.AccountStatus;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Role이 이미 "ROLE_"로 시작하는지 확인하고 추가
    String role = user.getRole().name();
    if (!role.startsWith("ROLE_")) {
      role = "ROLE_" + role;
    }
    return List.of(new SimpleGrantedAuthority(role));
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
  public boolean isAccountNonExpired() {
    // 계정 만료 여부 - 현재는 항상 true로 설정
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // 계정 잠금 여부 - 현재는 항상 true로 설정
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // 자격 증명 만료 여부 - 현재는 항상 true로 설정
    return true;
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

  // 사용자 Role을 직접 접근할 수 있는 편의 메서드
  public UserRole getRole() {
    return user.getRole();
  }
}